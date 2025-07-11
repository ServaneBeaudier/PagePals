package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.AdresseDetailsDTO;
import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;
import com.pagepals.circle.model.ModeRencontre;

class UpdateCircleDTOTest {

    @Test
    void testGettersSetters() {
        UpdateCircleDTO dto = new UpdateCircleDTO();

        dto.setId(10L);
        dto.setNom("Nouveau Cercle");
        dto.setDescription("Description mise à jour");
        dto.setModeRencontre(ModeRencontre.ENLIGNE);
        dto.setDateRencontre(LocalDateTime.of(2025, 7, 25, 20, 0));
        dto.setNbMaxMembres(12);
        dto.setGenreIds(List.of(3L, 4L));
        dto.setLieuRencontre("Lyon");
        dto.setLieuRencontreDetails(new AdresseDetailsDTO("Shop", "15", "Rue", "69000", "Lyon"));
        dto.setLienVisio("https://meet.link");
        dto.setLivrePropose(BookDTO.builder().titre("Livre Maj").build());

        assertEquals(10L, dto.getId());
        assertEquals("Nouveau Cercle", dto.getNom());
        assertEquals("Description mise à jour", dto.getDescription());
        assertEquals(ModeRencontre.ENLIGNE, dto.getModeRencontre());
        assertEquals(LocalDateTime.of(2025, 7, 25, 20, 0), dto.getDateRencontre());
        assertEquals(12, dto.getNbMaxMembres());
        assertEquals(List.of(3L, 4L), dto.getGenreIds());
        assertEquals("Lyon", dto.getLieuRencontre());
        assertNotNull(dto.getLieuRencontreDetails());
        assertEquals("https://meet.link", dto.getLienVisio());
        assertNotNull(dto.getLivrePropose());
    }
}
