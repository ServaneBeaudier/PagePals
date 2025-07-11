package com.pagepals.search.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.pagepals.search.dto.CircleDTO;

public class CircleDTOTest {

    @Test
    void getterSetterTest() {
        CircleDTO dto = new CircleDTO();
        dto.setId(1L);
        dto.setNom("Test Circle");
        dto.setDescription("Description test");
        dto.setModeRencontre("PRESENTIEL");
        dto.setLieuRencontre("Bibliothèque");
        dto.setLienVisio("https://zoom.com/abc");
        dto.setDateRencontre(LocalDate.of(2025, 7, 11));
        dto.setGenres(List.of("Fiction", "Roman"));
        dto.setNbMaxMembres(15);
        dto.setNombreInscrits(5);
        dto.setEstOuvert(true);

        assertEquals(1L, dto.getId());
        assertEquals("Test Circle", dto.getNom());
        assertEquals("Description test", dto.getDescription());
        assertEquals("PRESENTIEL", dto.getModeRencontre());
        assertEquals("Bibliothèque", dto.getLieuRencontre());
        assertEquals("https://zoom.com/abc", dto.getLienVisio());
        assertEquals(LocalDate.of(2025, 7, 11), dto.getDateRencontre());
        assertEquals(List.of("Fiction", "Roman"), dto.getGenres());
        assertEquals(15, dto.getNbMaxMembres());
        assertEquals(5, dto.getNombreInscrits());
        assertTrue(dto.isEstOuvert());
    }

    @Test
    void builderTest() {
        CircleDTO dto = CircleDTO.builder()
                .id(2L)
                .nom("Builder Circle")
                .modeRencontre("ENLIGNE")
                .estOuvert(false)
                .build();

        assertEquals(2L, dto.getId());
        assertEquals("Builder Circle", dto.getNom());
        assertEquals("ENLIGNE", dto.getModeRencontre());
        assertFalse(dto.isEstOuvert());
    }

    @Test
    void circleDTOEqualsAndHashCode() {
        CircleDTO dto1 = CircleDTO.builder()
                .id(1L)
                .nom("Cercle 1")
                .modeRencontre("PRESENTIEL")
                .nbMaxMembres(10)
                .estOuvert(true)
                .build();

        CircleDTO dto2 = CircleDTO.builder()
                .id(1L)
                .nom("Cercle 1")
                .modeRencontre("PRESENTIEL")
                .nbMaxMembres(10)
                .estOuvert(true)
                .build();

        CircleDTO dto3 = CircleDTO.builder()
                .id(2L)
                .nom("Cercle 2")
                .modeRencontre("ENLIGNE")
                .nbMaxMembres(5)
                .estOuvert(false)
                .build();

        Object otherType = new Object();

        assertEquals(dto1, dto1); // égalité réflexive
        assertNotEquals(dto1, null); // pas égal à null
        assertNotEquals(dto1, otherType); // pas égal à un autre type
        assertEquals(dto1, dto2); // égalité avec un objet équivalent
        assertEquals(dto1.hashCode(), dto2.hashCode()); // hashCode idem
        assertNotEquals(dto1, dto3); // inégalité avec un objet différent
    }

    @Test
    void toStringTest() {
        CircleDTO dto = CircleDTO.builder().id(1L).nom("Circle").build();
        String str = dto.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("nom=Circle"));
    }
}
