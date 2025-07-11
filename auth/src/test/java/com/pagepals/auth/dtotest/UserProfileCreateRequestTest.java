package com.pagepals.auth.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.dto.UserProfileCreateRequest;

import java.time.LocalDate;

class UserProfileCreateRequestTest {

    @Test
    void testGettersSetters() {
        UserProfileCreateRequest dto = new UserProfileCreateRequest();

        dto.setId(789L);
        assertEquals(789L, dto.getId());

        LocalDate now = LocalDate.now();
        dto.setDateInscription(now);
        assertEquals(now, dto.getDateInscription());

        dto.setPseudo("monPseudo");
        assertEquals("monPseudo", dto.getPseudo());
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfileCreateRequest dto1 = new UserProfileCreateRequest();
        dto1.setId(1L);
        dto1.setDateInscription(LocalDate.of(2023, 1, 1));
        dto1.setPseudo("pseudo");

        UserProfileCreateRequest dto2 = new UserProfileCreateRequest();
        dto2.setId(1L);
        dto2.setDateInscription(LocalDate.of(2023, 1, 1));
        dto2.setPseudo("pseudo");

        UserProfileCreateRequest dto3 = new UserProfileCreateRequest();
        dto3.setId(2L);
        dto3.setDateInscription(LocalDate.of(2024, 1, 1));
        dto3.setPseudo("pseudo2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        UserProfileCreateRequest dto = new UserProfileCreateRequest();
        dto.setId(1L);
        dto.setDateInscription(LocalDate.of(2023, 1, 1));
        dto.setPseudo("pseudo");

        String toString = dto.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("2023"));
        assertTrue(toString.contains("pseudo"));
    }
}
