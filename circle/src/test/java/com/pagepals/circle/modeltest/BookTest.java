package com.pagepals.circle.modeltest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.model.Book;

class BookTest {

    @Test
    void testGettersSetters() {
        Book book = new Book();

        book.setId(1L);
        book.setTitre("Titre Livre");
        book.setAuteurs(List.of("Auteur1", "Auteur2"));
        book.setIsbn("123456789");
        book.setGenre("Roman");
        book.setCouvertureUrl("urlimage");

        assertEquals(1L, book.getId());
        assertEquals("Titre Livre", book.getTitre());
        assertEquals(List.of("Auteur1", "Auteur2"), book.getAuteurs());
        assertEquals("123456789", book.getIsbn());
        assertEquals("Roman", book.getGenre());
        assertEquals("urlimage", book.getCouvertureUrl());
    }

    @Test
    void testBuilder() {
        Book book = Book.builder()
                .id(1L)
                .titre("Titre Livre")
                .auteurs(List.of("Auteur1", "Auteur2"))
                .isbn("123456789")
                .genre("Roman")
                .couvertureUrl("urlimage")
                .build();

        assertEquals(1L, book.getId());
        assertEquals("Titre Livre", book.getTitre());
        assertNotNull(book.getAuteurs());
        assertEquals(2, book.getAuteurs().size());
    }

    @Test
    void testEqualsAndHashCode() {
        Book book1 = Book.builder()
                .id(1L)
                .titre("Titre Livre")
                .auteurs(List.of("Auteur1", "Auteur2"))
                .isbn("123456789")
                .genre("Roman")
                .couvertureUrl("urlimage")
                .build();

        Book book2 = Book.builder()
                .id(1L)
                .titre("Titre Livre")
                .auteurs(List.of("Auteur1", "Auteur2"))
                .isbn("123456789")
                .genre("Roman")
                .couvertureUrl("urlimage")
                .build();

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void testToString() {
        Book book = Book.builder()
                .id(1L)
                .titre("Titre Livre")
                .auteurs(List.of("Auteur1", "Auteur2"))
                .isbn("123456789")
                .genre("Roman")
                .couvertureUrl("urlimage")
                .build();

        String str = book.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("titre=Titre Livre"));
        assertTrue(str.contains("isbn=123456789"));
        assertTrue(str.contains("genre=Roman"));
        assertTrue(str.contains("couvertureUrl=urlimage"));
    }
}
