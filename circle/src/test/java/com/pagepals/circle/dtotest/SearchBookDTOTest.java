package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.SearchBookDTO;

class SearchBookDTOTest {

    @Test
    void testGettersSetters() {
        SearchBookDTO dto = new SearchBookDTO();
        dto.setCritereRecherche("Harry Potter");
        assertEquals("Harry Potter", dto.getCritereRecherche());
    }
}
