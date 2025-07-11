package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.BookDTO;

class BookDTOTest {

    @Test
    void testGettersSetters() {
        BookDTO dto = new BookDTO();
        dto.setTitre("Le Petit Prince");
        dto.setAuteurs(List.of("Antoine de Saint-Exupéry"));
        dto.setIsbn("1234567890");
        dto.setGenre("Fiction");
        dto.setCouvertureUrl("http://image.url/couverture.jpg");

        assertEquals("Le Petit Prince", dto.getTitre());
        assertEquals(List.of("Antoine de Saint-Exupéry"), dto.getAuteurs());
        assertEquals("1234567890", dto.getIsbn());
        assertEquals("Fiction", dto.getGenre());
        assertEquals("http://image.url/couverture.jpg", dto.getCouvertureUrl());
    }

    @Test
    void testBuilder() {
        BookDTO dto = BookDTO.builder()
                .titre("Le Petit Prince")
                .auteurs(List.of("Antoine de Saint-Exupéry"))
                .isbn("1234567890")
                .genre("Fiction")
                .couvertureUrl("http://image.url/couverture.jpg")
                .build();

        assertEquals("Le Petit Prince", dto.getTitre());
        assertEquals(List.of("Antoine de Saint-Exupéry"), dto.getAuteurs());
        assertEquals("1234567890", dto.getIsbn());
        assertEquals("Fiction", dto.getGenre());
        assertEquals("http://image.url/couverture.jpg", dto.getCouvertureUrl());
    }
}
