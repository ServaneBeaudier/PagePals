package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.ParticipantDTO;

public class ParticipantDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        ParticipantDTO dto = new ParticipantDTO();

        dto.setId(10L);
        dto.setPseudo("pseudoTest");
        dto.setPhotoProfil("photo.png");

        assertEquals(10L, dto.getId());
        assertEquals("pseudoTest", dto.getPseudo());
        assertEquals("photo.png", dto.getPhotoProfil());
    }

    @Test
    void testAllArgsConstructor() {
        ParticipantDTO dto = new ParticipantDTO(1L, "pseudo1", "photo1.jpg");

        assertEquals(1L, dto.getId());
        assertEquals("pseudo1", dto.getPseudo());
        assertEquals("photo1.jpg", dto.getPhotoProfil());
    }

    @Test
    void testEqualsAndHashCode() {
        ParticipantDTO dto1 = new ParticipantDTO(1L, "pseudo", "photo");
        ParticipantDTO dto2 = new ParticipantDTO(1L, "pseudo", "photo");
        ParticipantDTO dto3 = new ParticipantDTO(2L, "pseudo2", "photo2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        ParticipantDTO dto = new ParticipantDTO(5L, "pseudo", "photo.jpg");
        String toString = dto.toString();
        assertTrue(toString.contains("id=5"));
        assertTrue(toString.contains("pseudo=pseudo"));
        assertTrue(toString.contains("photoProfil=photo.jpg"));
    }
}
