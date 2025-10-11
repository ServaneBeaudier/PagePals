package com.pagepals.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTGenerator {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private Key key;

    public Key getKey() {
        return key;
    }

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 🔹 Génère un token avec la durée par défaut (définie dans
     * application.properties)
     */
    public String generateToken(Long userId, String role, String email) {
        return generateToken(userId, role, email, expirationMs);
    }

    /**
     * 🔹 Génère un token avec une durée personnalisée (utile pour refresh token)
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

}
