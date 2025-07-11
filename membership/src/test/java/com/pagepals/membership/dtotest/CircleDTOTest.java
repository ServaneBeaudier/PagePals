package com.pagepals.membership.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pagepals.membership.dto.CircleDTO;
import com.pagepals.membership.model.ModeRencontre;

public class CircleDTOTest {

    @Test
    void testGettersSetters() {
        CircleDTO dto = new CircleDTO();
        dto.setId(10L);
        dto.setNom("Nom test");
        dto.setDescription("Desc test");
        dto.setDateRencontre(java.time.LocalDateTime.now());
        dto.setModeRencontre(ModeRencontre.PRESENTIEL);
        dto.setCreateurId(5L);
        dto.setNbMaxMembres(15);
        dto.setMembersCount(8);

        assertEquals(10L, dto.getId());
        assertEquals("Nom test", dto.getNom());
        assertEquals("Desc test", dto.getDescription());
        assertNotNull(dto.getDateRencontre());
        assertEquals(ModeRencontre.PRESENTIEL, dto.getModeRencontre());
        assertEquals(5L, dto.getCreateurId());
        assertEquals(15, dto.getNbMaxMembres());
        assertEquals(8, dto.getMembersCount());
    }

    @Test
    void testBuilder() {
        CircleDTO dto = CircleDTO.builder()
            .id(20L)
            .nom("Builder test")
            .description("Description builder")
            .dateRencontre(java.time.LocalDateTime.of(2025, 7, 12, 10, 0))
            .modeRencontre(ModeRencontre.ENLIGNE)
            .createurId(9L)
            .nbMaxMembres(25)
            .membersCount(12)
            .build();

        assertEquals(20L, dto.getId());
        assertEquals("Builder test", dto.getNom());
        assertEquals("Description builder", dto.getDescription());
        assertEquals(java.time.LocalDateTime.of(2025, 7, 12, 10, 0), dto.getDateRencontre());
        assertEquals(ModeRencontre.ENLIGNE, dto.getModeRencontre());
        assertEquals(9L, dto.getCreateurId());
        assertEquals(25, dto.getNbMaxMembres());
        assertEquals(12, dto.getMembersCount());
    }

    @Test
    void testEqualsHashCodeAndToString() {
        CircleDTO dto1 = CircleDTO.builder()
            .id(1L)
            .nom("Test")
            .build();

        CircleDTO dto2 = CircleDTO.builder()
            .id(1L)
            .nom("Test")
            .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("Test"));
    }
}
