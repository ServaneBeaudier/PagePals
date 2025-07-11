package com.pagepals.auth.jwttest;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.jwt.JWTGenerator;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

class JWTGeneratorTest {

    private JWTGenerator jwtGenerator;

    private final String secret = "ThisIsASecretKeyForJwtSigningMustBeLongEnough12345"; // au moins 32 bytes
    private final long expirationMs = 3600000; // 1h

    @BeforeEach
    void setUp() throws Exception {
        jwtGenerator = new JWTGenerator();

        // Injection des valeurs @Value via réflexion
        Field secretField = JWTGenerator.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtGenerator, secret);

        Field expirationField = JWTGenerator.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.setLong(jwtGenerator, expirationMs);

        // Appel manuel de init() pour initialiser la clé
        jwtGenerator.init();
    }

    @Test
    void init_shouldGenerateKey() {
        Key key = jwtGenerator.getKey();
        assertNotNull(key);
        assertEquals(secret.getBytes(StandardCharsets.UTF_8).length, key.getEncoded().length);
    }

    @Test
    void generateToken_shouldContainClaims() {
        Long userId = 123L;
        String role = "MEMBRE";
        String email = "test@example.com";

        String token = jwtGenerator.generateToken(userId, role, email);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtGenerator.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(role, claims.get("role", String.class));
        assertEquals(email, claims.getSubject());

        Date now = new Date();
        assertTrue(claims.getIssuedAt().before(now) || claims.getIssuedAt().equals(now));

        Date expectedExpiry = new Date(claims.getIssuedAt().getTime() + expirationMs);
        assertEquals(expectedExpiry.getTime(), claims.getExpiration().getTime());
    }
}
