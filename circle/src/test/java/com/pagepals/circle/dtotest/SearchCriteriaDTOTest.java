package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.SearchCriteriaDTO;

class SearchCriteriaDTOTest {

    @Test
    void testGettersSetters() {
        SearchCriteriaDTO dto = new SearchCriteriaDTO();

        dto.setMotCle("Roman");
        dto.setGenre("Fiction");
        dto.setFormat("Présentiel");
        dto.setDate(LocalDate.of(2025, 7, 15));
        dto.setDateExacte(LocalDate.of(2025, 7, 20));

        assertEquals("Roman", dto.getMotCle());
        assertEquals("Fiction", dto.getGenre());
        assertEquals("Présentiel", dto.getFormat());
        assertEquals(LocalDate.of(2025, 7, 15), dto.getDate());
        assertEquals(LocalDate.of(2025, 7, 20), dto.getDateExacte());
    }

    @Test
    void testBuilder() {
        SearchCriteriaDTO dto = SearchCriteriaDTO.builder()
            .motCle("Roman")
            .genre("Fiction")
            .format("Présentiel")
            .date(LocalDate.of(2025, 7, 15))
            .dateExacte(LocalDate.of(2025, 7, 20))
            .build();

        assertEquals("Roman", dto.getMotCle());
        assertEquals("Fiction", dto.getGenre());
        assertEquals("Présentiel", dto.getFormat());
        assertEquals(LocalDate.of(2025, 7, 15), dto.getDate());
        assertEquals(LocalDate.of(2025, 7, 20), dto.getDateExacte());
    }
}
