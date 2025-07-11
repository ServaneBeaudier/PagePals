package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.CircleDTO;

public class CircleDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        CircleDTO dto = new CircleDTO();

        dto.setId(123L);
        dto.setCreateurId(456L);
        dto.setGenres(List.of("Roman", "Science-fiction"));
        dto.setGenreIds(List.of(1L, 2L, 3L));

        assertEquals(123L, dto.getId());
        assertEquals(456L, dto.getCreateurId());
        assertEquals(List.of("Roman", "Science-fiction"), dto.getGenres());
        assertEquals(List.of(1L, 2L, 3L), dto.getGenreIds());
    }

    @Test
    void testEqualsAndHashCode() {
        CircleDTO dto1 = new CircleDTO();
        dto1.setId(1L);
        dto1.setCreateurId(10L);

        CircleDTO dto2 = new CircleDTO();
        dto2.setId(1L);
        dto2.setCreateurId(10L);

        // Avec Lombok @Data, equals et hashCode sont générés automatiquement
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        CircleDTO dto = new CircleDTO();
        dto.setId(5L);
        dto.setCreateurId(20L);
        dto.setGenres(List.of("Poésie"));
        dto.setGenreIds(List.of(7L));

        String str = dto.toString();

        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("createurId=20"));
        assertTrue(str.contains("genres=[Poésie]"));
        assertTrue(str.contains("genreIds=[7]"));
    }

}
