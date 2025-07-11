package com.pagepals.circle.modeltest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.model.AdresseDetails;
import com.pagepals.circle.model.Book;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.model.LiteraryGenre;
import com.pagepals.circle.model.ModeRencontre;

class CircleTest {

    private Circle createSampleCircle() {
        AdresseDetails lieuDetails = new AdresseDetails("Shop", "12", "Rue", "75001", "Paris");
        Book livre = new Book(1L, "Titre Livre", null, "123456789", "Roman", "urlimage");
        LiteraryGenre genre1 = new LiteraryGenre(1L, "Fiction");
        LiteraryGenre genre2 = new LiteraryGenre(2L, "Historique");
        Set<LiteraryGenre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        return Circle.builder()
                .id(10L)
                .nom("Cercle Test")
                .description("Description ici")
                .modeRencontre(ModeRencontre.PRESENTIEL)
                .dateCreation(LocalDate.of(2025, 7, 15))
                .dateRencontre(LocalDateTime.of(2025, 7, 20, 19, 0))
                .lieuRencontre("Paris")
                .lieuRencontreDetails(lieuDetails)
                .lienVisio(null)
                .nbMaxMembres(15)
                .memberCounts(10)
                .createurId(5L)
                .livrePropose(livre)
                .isArchived(false)
                .genres(genres)
                .build();
    }

    @Test
    void testGettersSetters() {
        Circle circle = new Circle();

        AdresseDetails lieuDetails = new AdresseDetails("Shop", "12", "Rue", "75001", "Paris");
        Book livre = new Book(1L, "Titre Livre", null, "123456789", "Roman", "urlimage");
        Set<LiteraryGenre> genres = new HashSet<>();
        genres.add(new LiteraryGenre(1L, "Fiction"));

        circle.setId(10L);
        circle.setNom("Cercle Test");
        circle.setDescription("Description ici");
        circle.setModeRencontre(ModeRencontre.PRESENTIEL);
        circle.setDateCreation(LocalDate.of(2025, 7, 15));
        circle.setDateRencontre(LocalDateTime.of(2025, 7, 20, 19, 0));
        circle.setLieuRencontre("Paris");
        circle.setLieuRencontreDetails(lieuDetails);
        circle.setLienVisio(null);
        circle.setNbMaxMembres(15);
        circle.setMemberCounts(10);
        circle.setCreateurId(5L);
        circle.setLivrePropose(livre);
        circle.setArchived(false);
        circle.setGenres(genres);

        assertEquals(10L, circle.getId());
        assertEquals("Cercle Test", circle.getNom());
        assertEquals("Description ici", circle.getDescription());
        assertEquals(ModeRencontre.PRESENTIEL, circle.getModeRencontre());
        assertEquals(LocalDate.of(2025, 7, 15), circle.getDateCreation());
        assertEquals(LocalDateTime.of(2025, 7, 20, 19, 0), circle.getDateRencontre());
        assertEquals("Paris", circle.getLieuRencontre());
        assertEquals(lieuDetails, circle.getLieuRencontreDetails());
        assertNull(circle.getLienVisio());
        assertEquals(15, circle.getNbMaxMembres());
        assertEquals(10, circle.getMemberCounts());
        assertEquals(5L, circle.getCreateurId());
        assertEquals(livre, circle.getLivrePropose());
        assertFalse(circle.isArchived());
        assertEquals(genres, circle.getGenres());
    }

    @Test
    void testBuilder() {
        Circle circle = createSampleCircle();
        assertEquals(10L, circle.getId());
        assertEquals("Cercle Test", circle.getNom());
        assertEquals(ModeRencontre.PRESENTIEL, circle.getModeRencontre());
        assertNotNull(circle.getGenres());
        assertFalse(circle.getGenres().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Circle circle1 = createSampleCircle();
        Circle circle2 = createSampleCircle();

        assertEquals(circle1, circle2);
        assertEquals(circle1.hashCode(), circle2.hashCode());
    }

    @Test
    void testToString() {
        Circle circle = createSampleCircle();
        String str = circle.toString();

        assertTrue(str.contains("id=10"));
        assertTrue(str.contains("nom=Cercle Test"));
        assertTrue(str.contains("modeRencontre=PRESENTIEL"));
        assertTrue(str.contains("genres"));
        assertTrue(str.contains("isArchived=false"));
    }
}
