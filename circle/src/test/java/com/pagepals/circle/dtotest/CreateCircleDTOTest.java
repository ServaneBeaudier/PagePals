package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.AdresseDetailsDTO;
import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.dto.CreateCircleDTO;

class CreateCircleDTOTest {

    @Test
    void testGettersSetters() {
        CreateCircleDTO dto = new CreateCircleDTO();

        dto.setNom("Mon Cercle");
        dto.setDescription("Description cercle");
        dto.setModeRencontre(com.pagepals.circle.model.ModeRencontre.PRESENTIEL);
        dto.setDateRencontre(LocalDateTime.of(2025, 7, 20, 19, 0));
        dto.setNbMaxMembres(15);
        dto.setGenreIds(List.of(1L, 2L));
        dto.setLieuRencontre("Paris");
        dto.setLieuRencontreDetails(new AdresseDetailsDTO("Shop", "12", "Road", "75001", "Paris"));
        dto.setLienVisio("https://zoom.link");
        dto.setCreateurId(5L);
        dto.setLivrePropose(BookDTO.builder().titre("Livre").build());

        assertEquals("Mon Cercle", dto.getNom());
        assertEquals("Description cercle", dto.getDescription());
        assertEquals(com.pagepals.circle.model.ModeRencontre.PRESENTIEL, dto.getModeRencontre());
        assertEquals(LocalDateTime.of(2025, 7, 20, 19, 0), dto.getDateRencontre());
        assertEquals(15, dto.getNbMaxMembres());
        assertEquals(List.of(1L, 2L), dto.getGenreIds());
        assertEquals("Paris", dto.getLieuRencontre());
        assertNotNull(dto.getLieuRencontreDetails());
        assertEquals("https://zoom.link", dto.getLienVisio());
        assertEquals(5L, dto.getCreateurId());
        assertNotNull(dto.getLivrePropose());
    }
}
