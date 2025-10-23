package com.pagepals.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Composant responsable de la génération et de la validation des jetons JWT.
 * 
 * Utilisé par le service d'authentification pour émettre des jetons d'accès
 * et de rafraîchissement signés, puis vérifier leur validité.
 * 
 * Les paramètres de clé secrète et de durée d'expiration sont injectés depuis
 * le fichier de configuration (application.properties ou équivalent).
 */
@Component
public class JWTGenerator {

    /** Clé secrète utilisée pour signer les JWT. */
    @Value("${jwt.secret}")
    private String secret;

    /** Durée de validité par défaut des jetons, en millisecondes. */
    @Value("${jwt.expirationMs}")
    private long expirationMs;

    /** Clé HMAC dérivée du secret, utilisée pour la signature et la validation. */
    private Key key;

    /** Retourne la clé HMAC utilisée pour la signature et la vérification. */
    public Key getKey() {
        return key;
    }

    /**
     * Initialise la clé HMAC à partir de la chaîne secrète configurée.
     * Méthode appelée automatiquement après l'injection des propriétés.
     */
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Génère un jeton JWT avec la durée d'expiration par défaut.
     *
     * @param userId identifiant unique de l'utilisateur
     * @param role rôle attribué à l'utilisateur
     * @param email email associé à l'utilisateur
     * @return jeton JWT signé contenant les informations utilisateur
     */
    public String generateToken(Long userId, String role, String email) {
        return generateToken(userId, role, email, expirationMs);
    }

    /**
     * Génère un jeton JWT avec une durée d'expiration personnalisée.
     * Utile notamment pour les jetons de rafraîchissement.
     *
     * @param userId identifiant unique de l'utilisateur
     * @param role rôle attribué à l'utilisateur
     * @param email email associé à l'utilisateur
     * @param expirationMillis durée de validité du jeton en millisecondes
     * @return jeton JWT signé contenant les informations utilisateur
     */
    public String generateToken(Long userId, String role, String email, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Analyse et valide un jeton JWT.
     * Si le jeton est valide et non expiré, renvoie les informations (claims) qu’il contient.
     *
     * @param token jeton JWT à analyser
     * @return les claims extraits du token (ex. userId, email, rôle)
     * @throws io.jsonwebtoken.JwtException si le token est invalide ou expiré
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
