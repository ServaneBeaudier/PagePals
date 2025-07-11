package com.pagepals.auth.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.dto.AuthResponseDTO;

class AuthResponseDTOTest {

     @Test
    void testConstructorAndGettersSetters() {
        AuthResponseDTO dto = new AuthResponseDTO("token123", "user@example.com", "MEMBRE", 1L);

        assertEquals("token123", dto.getToken());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("MEMBRE", dto.getRole());
        assertEquals(1L, dto.getId());

        dto.setToken("newToken");
        assertEquals("newToken", dto.getToken());

        dto.setEmail("newuser@example.com");
        assertEquals("newuser@example.com", dto.getEmail());

        dto.setRole("ADMIN");
        assertEquals("ADMIN", dto.getRole());

        dto.setId(2L);
        assertEquals(2L, dto.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthResponseDTO dto1 = new AuthResponseDTO("token", "email", "role", 1L);
        AuthResponseDTO dto2 = new AuthResponseDTO("token", "email", "role", 1L);
        AuthResponseDTO dto3 = new AuthResponseDTO("tokenDiff", "email", "role", 1L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        AuthResponseDTO dto = new AuthResponseDTO("token", "email", "role", 1L);
        String toString = dto.toString();

        assertTrue(toString.contains("token"));
        assertTrue(toString.contains("email"));
        assertTrue(toString.contains("role"));
        assertTrue(toString.contains("1"));
    }
}