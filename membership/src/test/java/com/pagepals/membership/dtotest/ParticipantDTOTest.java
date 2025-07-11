package com.pagepals.membership.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pagepals.membership.dto.ParticipantDTO;

public class ParticipantDTOTest {

    @Test
    void testGettersSetters() {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(10L);
        dto.setPseudo("testPseudo");
        dto.setPhotoProfil("url/photo.jpg");

        assertEquals(10L, dto.getId());
        assertEquals("testPseudo", dto.getPseudo());
        assertEquals("url/photo.jpg", dto.getPhotoProfil());
    }

    @Test
    void testEqualsHashCodeAndToString() {
        ParticipantDTO dto1 = new ParticipantDTO(1L, "pseudo1", "photo1");
        ParticipantDTO dto2 = new ParticipantDTO(1L, "pseudo1", "photo1");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("pseudo"));
    }
}
