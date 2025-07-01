package com.pagepals.circle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pagepals.circle.dto.*;
import com.pagepals.circle.jwt.JWTUtil;
import com.pagepals.circle.service.CircleService;
import com.pagepals.circle.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/circles")
@RequiredArgsConstructor
public class CircleController {

    private final CircleService circleService;

    private final MessageService messageService;

    private final JWTUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<CircleDTO> getCircleById(@PathVariable Long id) {
        CircleDTO dto = circleService.getCircleById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("active")
    public ResponseEntity<List<CircleDTO>> getCirclesActive() {
        return ResponseEntity.ok(circleService.getCirclesActive());
    }

    @GetMapping("archived")
    public ResponseEntity<List<CircleDTO>> getCirclesArchived() {
        return ResponseEntity.ok(circleService.getCirclesArchived());
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createCircle(@RequestBody CreateCircleDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        circleService.createCircle(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateCircle(@RequestBody UpdateCircleDTO dto, @PathVariable long id,
            @RequestHeader("X-User-Id") Long userId) {
        dto.setId(id);
        circleService.updateCircle(dto, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{circleId}")
    public ResponseEntity<String> deleteCircle(@PathVariable Long circleId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        circleService.deleteCircle(circleId, userId);
        return ResponseEntity.ok("Cercle supprimé !");
    }

    @GetMapping("/{circleId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long circleId) {
        return ResponseEntity.ok(messageService.getMessagesByCircleId(circleId));
    }

    @PostMapping("/{circleId}/messages")
    public ResponseEntity<Void> envoyerMessage(@PathVariable Long circleId,
            @RequestBody MessageDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        // Extraire l’ID de l’auteur depuis le token
        String token = authHeader.replace("Bearer ", "");
        Long auteurId = jwtUtil.extractUserId(token);

        // Compléter le DTO
        dto.setCircleId(circleId);
        dto.setAuteurId(auteurId);

        messageService.sendMessage(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<CircleDTO>> searchCircles(@RequestBody SearchCriteriaDTO criteria) {
        List<CircleDTO> resultats = circleService.searchCircles(criteria);
        return ResponseEntity.ok(resultats);
    }
}
