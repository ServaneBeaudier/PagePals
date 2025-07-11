package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.MessageDTO;

class MessageDTOTest {

    @Test
    void testGettersSetters() {
        MessageDTO dto = new MessageDTO();

        dto.setId(10L);
        dto.setContenu("Contenu du message");
        dto.setAuteurId(5L);
        dto.setPseudoAuteur("Pseudo");
        dto.setPhotoAuteur("http://photo.url/image.jpg");
        dto.setDateEnvoi(LocalDateTime.of(2025, 7, 10, 15, 30));
        dto.setCircleId(3L);

        assertEquals(10L, dto.getId());
        assertEquals("Contenu du message", dto.getContenu());
        assertEquals(5L, dto.getAuteurId());
        assertEquals("Pseudo", dto.getPseudoAuteur());
        assertEquals("http://photo.url/image.jpg", dto.getPhotoAuteur());
        assertEquals(LocalDateTime.of(2025, 7, 10, 15, 30), dto.getDateEnvoi());
        assertEquals(3L, dto.getCircleId());
    }

    @Test
    void testBuilder() {
        MessageDTO dto = MessageDTO.builder()
                .id(10L)
                .contenu("Contenu du message")
                .auteurId(5L)
                .pseudoAuteur("Pseudo")
                .photoAuteur("http://photo.url/image.jpg")
                .dateEnvoi(LocalDateTime.of(2025, 7, 10, 15, 30))
                .circleId(3L)
                .build();

        assertEquals(10L, dto.getId());
        assertEquals("Contenu du message", dto.getContenu());
        assertEquals(5L, dto.getAuteurId());
        assertEquals("Pseudo", dto.getPseudoAuteur());
        assertEquals("http://photo.url/image.jpg", dto.getPhotoAuteur());
        assertEquals(LocalDateTime.of(2025, 7, 10, 15, 30), dto.getDateEnvoi());
        assertEquals(3L, dto.getCircleId());
    }
}
