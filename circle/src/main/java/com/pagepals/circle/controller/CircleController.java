package com.pagepals.circle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;
import com.pagepals.circle.jwt.JWTUtil;
import com.pagepals.circle.service.CircleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/circles")
@RequiredArgsConstructor
public class CircleController {

    private final CircleService circleService;

    private final JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<Void> createCircle(@RequestBody CreateCircleDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        circleService.createCircle(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateCircle(@RequestBody UpdateCircleDTO dto) {
        circleService.updateCircle(dto);
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

}
