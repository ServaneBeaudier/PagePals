package com.pagepals.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Représente la réponse envoyée après une authentification réussie ou un rafraîchissement de jeton.
 * Contient le jeton d'accès, le refresh token, ainsi que les informations essentielles de l'utilisateur.
 *
 */
@Data
@AllArgsConstructor
public class AuthResponseDTO {
    
    private String token;
    private String refreshToken;
    private String email;
    private String role;
    private Long id;
}

