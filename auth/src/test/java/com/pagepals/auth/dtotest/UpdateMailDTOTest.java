package com.pagepals.auth.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.dto.UpdateMailDTO;

class UpdateMailDTOTest {

    @Test
    void testGettersSetters() {
        UpdateMailDTO dto = new UpdateMailDTO();

        dto.setUserId(123L);
        assertEquals(123L, dto.getUserId());

        dto.setNewEmail("newemail@example.com");
        assertEquals("newemail@example.com", dto.getNewEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateMailDTO dto1 = new UpdateMailDTO();
        dto1.setUserId(1L);
        dto1.setNewEmail("email@example.com");

        UpdateMailDTO dto2 = new UpdateMailDTO();
        dto2.setUserId(1L);
        dto2.setNewEmail("email@example.com");

        UpdateMailDTO dto3 = new UpdateMailDTO();
        dto3.setUserId(2L);
        dto3.setNewEmail("other@example.com");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        UpdateMailDTO dto = new UpdateMailDTO();
        dto.setUserId(1L);
        dto.setNewEmail("email@example.com");

        String toString = dto.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("email@example.com"));
    }
}
