package com.pagepals.auth.controller;

import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.dto.UpdateMailDTO;
import com.pagepals.auth.dto.UpdatePasswordDTO;
import com.pagepals.auth.model.UserEntity;
import com.pagepals.auth.service.AuthService;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> getEmail(@RequestParam("id") Long userId) {
        String email = authService.getEmailByUserId(userId);
        if (email == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> response = Collections.singletonMap("email", email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-email")
    public ResponseEntity<Void> updateEmail(@RequestBody @Valid UpdateMailDTO dto) {
        authService.updateMail(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        authService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cleanup/{userId}")
    public ResponseEntity<Void> cleanupAuthUser(@PathVariable Long userId) {
        authService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Claims claims = authService.parseToken(refreshToken);
            Long userId = Long.valueOf(String.valueOf(claims.get("userId")));
            String email = claims.getSubject();

            UserEntity user = authService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String newAccessToken = authService.generateAccessToken(user);
            String newRefreshToken = authService.generateRefreshToken(user);

            return ResponseEntity.ok(new AuthResponseDTO(
                    newAccessToken,
                    newRefreshToken,
                    user.getEmail(),
                    user.getRole().name(),
                    user.getId()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
