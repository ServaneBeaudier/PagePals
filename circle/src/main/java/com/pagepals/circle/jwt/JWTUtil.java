package com.pagepals.circle.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilitaire de gestion des tokens JWT pour le microservice circle-service.
 * 
 * Permet d'extraire les informations d'un utilisateur à partir d'un jeton JWT,
 * notamment l'identifiant de l'utilisateur.
 */
@Component
public class JWTUtil {

    /** Clé secrète utilisée pour la vérification de la signature du token. */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Extrait l'identifiant de l'utilisateur depuis un token JWT signé.
     *
     * @param token jeton JWT à analyser
     * @return identifiant de l'utilisateur contenu dans les claims du token
     */
    public Long extractUserId(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.get("userId", Long.class);
    }
}
