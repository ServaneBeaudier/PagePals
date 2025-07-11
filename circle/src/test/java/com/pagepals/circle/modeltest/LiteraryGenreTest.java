package com.pagepals.circle.modeltest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.pagepals.circle.model.LiteraryGenre;

class LiteraryGenreTest {

    @Test
    void testGettersSetters() {
        LiteraryGenre genre = new LiteraryGenre();

        genre.setId(1L);
        genre.setNomGenre("Roman");

        assertEquals(1L, genre.getId());
        assertEquals("Roman", genre.getNomGenre());
    }

    @Test
    void testBuilder() {
        LiteraryGenre genre = LiteraryGenre.builder()
                .id(1L)
                .nomGenre("Roman")
                .build();

        assertEquals(1L, genre.getId());
        assertEquals("Roman", genre.getNomGenre());
    }

    @Test
    void testEqualsAndHashCode() {
        LiteraryGenre genre1 = LiteraryGenre.builder()
                .id(1L)
                .nomGenre("Roman")
                .build();

        LiteraryGenre genre2 = LiteraryGenre.builder()
                .id(1L)
                .nomGenre("Roman")
                .build();

        assertEquals(genre1, genre2);
        assertEquals(genre1.hashCode(), genre2.hashCode());
    }

    @Test
    void testToString() {
        LiteraryGenre genre = LiteraryGenre.builder()
                .id(1L)
                .nomGenre("Roman")
                .build();

        String str = genre.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("nomGenre=Roman"));
    }
}
