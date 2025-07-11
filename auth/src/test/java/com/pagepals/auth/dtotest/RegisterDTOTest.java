package com.pagepals.auth.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.dto.RegisterDTO;

class RegisterDTOTest {

    @Test
    void testGettersSetters() {
        RegisterDTO dto = new RegisterDTO();

        dto.setEmail("user@example.com");
        assertEquals("user@example.com", dto.getEmail());

        dto.setMotDePasse("password");
        assertEquals("password", dto.getMotDePasse());

        dto.setPseudo("user123");
        assertEquals("user123", dto.getPseudo());
    }

    @Test
    void testEqualsAndHashCode() {
        RegisterDTO dto1 = new RegisterDTO();
        dto1.setEmail("email@example.com");
        dto1.setMotDePasse("password");
        dto1.setPseudo("pseudo");

        RegisterDTO dto2 = new RegisterDTO();
        dto2.setEmail("email@example.com");
        dto2.setMotDePasse("password");
        dto2.setPseudo("pseudo");

        RegisterDTO dto3 = new RegisterDTO();
        dto3.setEmail("diff@example.com");
        dto3.setMotDePasse("password");
        dto3.setPseudo("pseudo");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("email@example.com");
        dto.setMotDePasse("password");
        dto.setPseudo("pseudo");

        String toString = dto.toString();

        assertTrue(toString.contains("email@example.com"));
        assertTrue(toString.contains("password"));
        assertTrue(toString.contains("pseudo"));
    }
}
