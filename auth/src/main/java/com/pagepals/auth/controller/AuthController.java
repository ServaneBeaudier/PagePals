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

/**
 * Contrôleur REST exposant les endpoints d'authentification et de gestion de
 * compte.
 * Centralise l'inscription, la connexion, la mise à jour des informations
 * sensibles
 * (email, mot de passe), la suppression des données d'auth et le
 * rafraîchissement de jeton.
 *
 * Les validations de payload sont assurées via @Valid. La logique métier est
 * déléguée à AuthService.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Récupère l'email d'un utilisateur à partir de son identifiant.
     *
     * @param userId identifiant technique de l'utilisateur
     * @return 200 avec {"email": "<email>"} si trouvé, sinon 404
     */
    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> getEmail(@RequestParam("id") Long userId) {
        String email = authService.getEmailByUserId(userId);
        if (email == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> response = Collections.singletonMap("email", email);
        return ResponseEntity.ok(response);
    }

    /**
     * Inscrit un nouvel utilisateur et émet les jetons d'authentification associés.
     *
     * @param dto informations d'inscription (ex. email, mot de passe)
     * @return 200 avec les jetons (access et refresh) et les métadonnées
     *         utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Authentifie un utilisateur et renvoie les jetons d'authentification.
     *
     * @param dto identifiants de connexion (ex. email, mot de passe)
     * @return 200 avec les jetons (access et refresh) et les métadonnées
     *         utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Met à jour l'email de l'utilisateur connecté ou ciblé selon la politique du service.
     *
     * @param dto payload contenant l'ancien et le nouvel email et/ou l'identifiant
     * @return 204 si la mise à jour a réussi
     */
    @PutMapping("/update-email")
    public ResponseEntity<Void> updateEmail(@RequestBody @Valid UpdateMailDTO dto) {
        authService.updateMail(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Met à jour le mot de passe d'un utilisateur.
     *
     * @param dto payload contenant l'ancien et le nouveau mot de passe et/ou l'identifiant
     * @return 204 si la mise à jour a réussi
     */
    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        authService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime les données d'authentification liées à un utilisateur.
     * À utiliser lors d'une suppression de compte pour nettoyer l'auth-service.
     *
     * @param userId identifiant de l'utilisateur
     * @return 204 si la suppression a réussi
     */
    @DeleteMapping("/cleanup/{userId}")
    public ResponseEntity<Void> cleanupAuthUser(@PathVariable Long userId) {
        authService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Rafraîchit le jeton d'accès à partir d'un refresh token valide.
     * Le body doit contenir la clé "refreshToken".
     *
     * Cas d'erreur:
     * - 400 si le refreshToken est absent ou vide
     * - 401 si le token est invalide, expiré, ou si l'utilisateur lié n'existe plus
     *
     * @param request map contenant le refresh token sous la clé "refreshToken"
     * @return 200 avec un nouveau couple (access, refresh) si succès, sinon 400/401
     */
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
