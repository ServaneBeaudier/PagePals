package com.pagepals.auth.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.dto.LoginDTO;

class LoginDTOTest {

    @Test
    void testGettersSetters() {
        LoginDTO dto = new LoginDTO();

        dto.setEmail("user@example.com");
        assertEquals("user@example.com", dto.getEmail());

        dto.setMotDePasse("password123");
        assertEquals("password123", dto.getMotDePasse());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginDTO dto1 = new LoginDTO();
        dto1.setEmail("email@example.com");
        dto1.setMotDePasse("password");

        LoginDTO dto2 = new LoginDTO();
        dto2.setEmail("email@example.com");
        dto2.setMotDePasse("password");

        LoginDTO dto3 = new LoginDTO();
        dto3.setEmail("diff@example.com");
        dto3.setMotDePasse("password");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("email@example.com");
        dto.setMotDePasse("password");

        String toString = dto.toString();

        assertTrue(toString.contains("email@example.com"));
        assertTrue(toString.contains("password"));
    }
}
