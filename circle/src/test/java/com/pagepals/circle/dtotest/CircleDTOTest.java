package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.AdresseDetailsDTO;
import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.dto.CircleDTO;
import com.pagepals.circle.model.ModeRencontre;

class CircleDTOTest {

    @Test
    void testGettersSetters() {
        CircleDTO dto = new CircleDTO();
        dto.setId(42L);
        dto.setNom("Nom Cercle");
        dto.setDescription("Description cercle");
        dto.setModeRencontre(ModeRencontre.PRESENTIEL);
        dto.setDateCreation(LocalDate.of(2025, 7, 10));
        dto.setDateRencontre(LocalDateTime.of(2025, 7, 20, 18, 30));
        dto.setNbMaxMembres(15);
        dto.setMembersCount(10);
        dto.setGenres(List.of("Fiction", "Aventure"));
        dto.setGenreIds(List.of(1L, 2L));
        dto.setLieuRencontre("Paris");
        dto.setLieuRencontreDetails(new AdresseDetailsDTO("Shop", "12", "Road", "75001", "Paris"));
        dto.setLienVisio("https://zoom.us/abc123");
        dto.setCreateurId(5L);
        dto.setLivrePropose(BookDTO.builder().titre("Livre Test").build());
        dto.setArchived(true);

        assertEquals(42L, dto.getId());
        assertEquals("Nom Cercle", dto.getNom());
        assertEquals("Description cercle", dto.getDescription());
        assertEquals(ModeRencontre.PRESENTIEL, dto.getModeRencontre());
        assertEquals(LocalDate.of(2025, 7, 10), dto.getDateCreation());
        assertEquals(LocalDateTime.of(2025, 7, 20, 18, 30), dto.getDateRencontre());
        assertEquals(15, dto.getNbMaxMembres());
        assertEquals(10, dto.getMembersCount());
        assertEquals(List.of("Fiction", "Aventure"), dto.getGenres());
        assertEquals(List.of(1L, 2L), dto.getGenreIds());
        assertEquals("Paris", dto.getLieuRencontre());
        assertNotNull(dto.getLieuRencontreDetails());
        assertEquals("https://zoom.us/abc123", dto.getLienVisio());
        assertEquals(5L, dto.getCreateurId());
        assertNotNull(dto.getLivrePropose());
        assertTrue(dto.isArchived());
    }

    @Test
    void testBuilder() {
        AdresseDetailsDTO adresse = new AdresseDetailsDTO("Shop", "12", "Road", "75001", "Paris");
        BookDTO book = BookDTO.builder().titre("Livre Test").build();

        CircleDTO dto = CircleDTO.builder()
                .id(42L)
                .nom("Nom Cercle")
                .description("Description cercle")
                .modeRencontre(ModeRencontre.PRESENTIEL)
                .dateCreation(LocalDate.of(2025, 7, 10))
                .dateRencontre(LocalDateTime.of(2025, 7, 20, 18, 30))
                .nbMaxMembres(15)
                .membersCount(10)
                .genres(List.of("Fiction", "Aventure"))
                .genreIds(List.of(1L, 2L))
                .lieuRencontre("Paris")
                .lieuRencontreDetails(adresse)
                .lienVisio("https://zoom.us/abc123")
                .createurId(5L)
                .livrePropose(book)
                .isArchived(true)
                .build();

        assertEquals(42L, dto.getId());
        assertEquals("Nom Cercle", dto.getNom());
        assertEquals("Description cercle", dto.getDescription());
        assertEquals(ModeRencontre.PRESENTIEL, dto.getModeRencontre());
        assertEquals(LocalDate.of(2025, 7, 10), dto.getDateCreation());
        assertEquals(LocalDateTime.of(2025, 7, 20, 18, 30), dto.getDateRencontre());
        assertEquals(15, dto.getNbMaxMembres());
        assertEquals(10, dto.getMembersCount());
        assertEquals(List.of("Fiction", "Aventure"), dto.getGenres());
        assertEquals(List.of(1L, 2L), dto.getGenreIds());
        assertEquals("Paris", dto.getLieuRencontre());
        assertEquals(adresse, dto.getLieuRencontreDetails());
        assertEquals("https://zoom.us/abc123", dto.getLienVisio());
        assertEquals(5L, dto.getCreateurId());
        assertEquals(book, dto.getLivrePropose());
        assertTrue(dto.isArchived());
    }
}
