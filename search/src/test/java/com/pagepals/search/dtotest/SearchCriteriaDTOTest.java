package com.pagepals.search.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.pagepals.search.dto.SearchCriteriaDTO;

public class SearchCriteriaDTOTest {

    @Test
    void getterSetterTest() {
        SearchCriteriaDTO dto = new SearchCriteriaDTO();
        dto.setMotCle("livre");
        dto.setGenre("Roman");
        dto.setFormat("PRESENTIEL");
        dto.setDate(LocalDate.of(2025, 7, 15));
        dto.setDateExacte(LocalDate.of(2025, 7, 20));
        dto.setEstOuvert(true);

        assertEquals("livre", dto.getMotCle());
        assertEquals("Roman", dto.getGenre());
        assertEquals("PRESENTIEL", dto.getFormat());
        assertEquals(LocalDate.of(2025, 7, 15), dto.getDate());
        assertEquals(LocalDate.of(2025, 7, 20), dto.getDateExacte());
        assertTrue(dto.getEstOuvert());
    }

    @Test
    void builderTest() {
        SearchCriteriaDTO dto = SearchCriteriaDTO.builder()
                .motCle("fiction")
                .genre("Policier")
                .format("ENLIGNE")
                .estOuvert(false)
                .build();

        assertEquals("fiction", dto.getMotCle());
        assertEquals("Policier", dto.getGenre());
        assertEquals("ENLIGNE", dto.getFormat());
        assertFalse(dto.getEstOuvert());
    }

    @Test
    void equalsAndHashCodeMoreComplete() {
        SearchCriteriaDTO dto1 = SearchCriteriaDTO.builder().motCle("test").build();
        SearchCriteriaDTO dto2 = SearchCriteriaDTO.builder().motCle("test").build();
        SearchCriteriaDTO dto3 = SearchCriteriaDTO.builder().motCle("différent").build();
        Object otherType = new Object();

        // égalité avec soi-même
        assertEquals(dto1, dto1);
        // égalité avec null
        assertNotEquals(dto1, null);
        // égalité avec un autre type
        assertNotEquals(dto1, otherType);
        // égalité entre deux objets égaux
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        // inégalité entre deux objets différents
        assertNotEquals(dto1, dto3);
    }

    @Test
    void toStringTest() {
        SearchCriteriaDTO dto = SearchCriteriaDTO.builder()
                .motCle("essai")
                .build();
        String str = dto.toString();
        assertTrue(str.contains("motCle=essai"));
    }
}
