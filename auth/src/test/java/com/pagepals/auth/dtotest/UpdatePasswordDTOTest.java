package com.pagepals.auth.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.dto.UpdatePasswordDTO;

class UpdatePasswordDTOTest {

    @Test
    void testGettersSetters() {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();

        dto.setUserId(456L);
        assertEquals(456L, dto.getUserId());

        dto.setNewMotDePasse("newpass12");
        assertEquals("newpass12", dto.getNewMotDePasse());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdatePasswordDTO dto1 = new UpdatePasswordDTO();
        dto1.setUserId(1L);
        dto1.setNewMotDePasse("password");

        UpdatePasswordDTO dto2 = new UpdatePasswordDTO();
        dto2.setUserId(1L);
        dto2.setNewMotDePasse("password");

        UpdatePasswordDTO dto3 = new UpdatePasswordDTO();
        dto3.setUserId(2L);
        dto3.setNewMotDePasse("diffpass");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setUserId(1L);
        dto.setNewMotDePasse("password");

        String toString = dto.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("password"));
    }
}
