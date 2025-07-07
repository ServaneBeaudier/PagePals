package com.pagepals.circle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pagepals.circle.dto.*;
import com.pagepals.circle.exception.CircleNotFoundException;
import com.pagepals.circle.jwt.JWTUtil;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.repository.CircleRepository;
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

    private final CircleRepository circleRepository;

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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCircle(@RequestBody UpdateCircleDTO dto, @PathVariable long id,
            @RequestHeader("X-User-Id") Long userId) {
        dto.setId(id);
        circleService.updateCircle(dto, userId);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/{id}/max-membres")
    public int getMaxMembres(@PathVariable Long id) {
        Circle circle = circleRepository.findById(id)
                .orElseThrow(() -> new CircleNotFoundException("Cercle introuvable"));

        return circle.getNbMaxMembres();
    }

    @GetMapping("/created-by/{userId}")
    public List<CircleDTO> getCirclesByCreateur(@PathVariable Long userId) {
        return circleService.findCirclesByCreateur(userId);
    }

    @DeleteMapping("/active-by-createur/{userId}")
    public ResponseEntity<Void> deleteActiveCirclesByCreateur(@PathVariable Long userId) {
        circleService.deleteActiveCirclesByCreateur(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/anonymize-user/{userId}")
    public ResponseEntity<Void> anonymizeUserInArchivedCircles(@PathVariable Long userId) {
        circleService.anonymizeUserInArchivedCircles(userId);
        return ResponseEntity.noContent().build();
    }
}
