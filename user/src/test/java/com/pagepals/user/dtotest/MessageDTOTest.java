package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.MessageDTO;

public class MessageDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        MessageDTO dto = new MessageDTO();

        dto.setId(10L);
        dto.setContenu("Hello world");
        dto.setAuteurId(20L);
        dto.setPseudoAuteur("user123");
        dto.setPhotoAuteur("photo.png");
        dto.setDateEnvoi(LocalDateTime.of(2023, 7, 10, 12, 0));
        dto.setCircleId(5L);

        assertEquals(10L, dto.getId());
        assertEquals("Hello world", dto.getContenu());
        assertEquals(20L, dto.getAuteurId());
        assertEquals("user123", dto.getPseudoAuteur());
        assertEquals("photo.png", dto.getPhotoAuteur());
        assertEquals(LocalDateTime.of(2023, 7, 10, 12, 0), dto.getDateEnvoi());
        assertEquals(5L, dto.getCircleId());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.of(2023, 7, 10, 15, 30);
        MessageDTO dto = new MessageDTO(
            1L,
            "Contenu test",
            2L,
            "pseudo",
            "photo.jpg",
            date,
            3L
        );

        assertEquals(1L, dto.getId());
        assertEquals("Contenu test", dto.getContenu());
        assertEquals(2L, dto.getAuteurId());
        assertEquals("pseudo", dto.getPseudoAuteur());
        assertEquals("photo.jpg", dto.getPhotoAuteur());
        assertEquals(date, dto.getDateEnvoi());
        assertEquals(3L, dto.getCircleId());
    }

    @Test
    void testBuilder() {
        LocalDateTime date = LocalDateTime.of(2023, 8, 1, 9, 45);
        MessageDTO dto = MessageDTO.builder()
            .id(99L)
            .contenu("Message via builder")
            .auteurId(77L)
            .pseudoAuteur("pseudoBuilder")
            .photoAuteur("photoBuilder.png")
            .dateEnvoi(date)
            .circleId(11L)
            .build();

        assertEquals(99L, dto.getId());
        assertEquals("Message via builder", dto.getContenu());
        assertEquals(77L, dto.getAuteurId());
        assertEquals("pseudoBuilder", dto.getPseudoAuteur());
        assertEquals("photoBuilder.png", dto.getPhotoAuteur());
        assertEquals(date, dto.getDateEnvoi());
        assertEquals(11L, dto.getCircleId());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime date = LocalDateTime.now();
        MessageDTO dto1 = new MessageDTO(1L, "txt", 1L, "pseudo", "photo", date, 2L);
        MessageDTO dto2 = new MessageDTO(1L, "txt", 1L, "pseudo", "photo", date, 2L);
        MessageDTO dto3 = new MessageDTO(2L, "diff", 2L, "pseudo2", "photo2", date, 3L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        MessageDTO dto = new MessageDTO();
        dto.setId(5L);
        dto.setContenu("content");
        dto.setAuteurId(10L);
        dto.setPseudoAuteur("pseudo");
        dto.setPhotoAuteur("photo");
        dto.setDateEnvoi(LocalDateTime.of(2023, 6, 1, 10, 0));
        dto.setCircleId(7L);

        String toString = dto.toString();
        assertTrue(toString.contains("id=5"));
        assertTrue(toString.contains("contenu=content"));
        assertTrue(toString.contains("auteurId=10"));
        assertTrue(toString.contains("pseudoAuteur=pseudo"));
        assertTrue(toString.contains("photoAuteur=photo"));
        assertTrue(toString.contains("dateEnvoi=2023-06-01T10:00"));
        assertTrue(toString.contains("circleId=7"));
    }
}
