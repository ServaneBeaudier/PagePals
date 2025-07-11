package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.UpdateUserProfileDTO;

public class UpdateUserProfileDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();

        dto.setId(10L);
        dto.setPseudo("pseudoTest");
        dto.setBio("Une bio");
        dto.setPhotoProfil("photo.png");

        assertEquals(10L, dto.getId());
        assertEquals("pseudoTest", dto.getPseudo());
        assertEquals("Une bio", dto.getBio());
        assertEquals("photo.png", dto.getPhotoProfil());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateUserProfileDTO dto1 = new UpdateUserProfileDTO();
        dto1.setId(1L);
        dto1.setPseudo("pseudo");
        dto1.setBio("bio");
        dto1.setPhotoProfil("photo");

        UpdateUserProfileDTO dto2 = new UpdateUserProfileDTO();
        dto2.setId(1L);
        dto2.setPseudo("pseudo");
        dto2.setBio("bio");
        dto2.setPhotoProfil("photo");

        UpdateUserProfileDTO dto3 = new UpdateUserProfileDTO();
        dto3.setId(2L);
        dto3.setPseudo("pseudo2");
        dto3.setBio("bio2");
        dto3.setPhotoProfil("photo2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setId(5L);
        dto.setPseudo("pseudo");
        dto.setBio("bio text");
        dto.setPhotoProfil("photo.jpg");

        String toString = dto.toString();
        assertTrue(toString.contains("id=5"));
        assertTrue(toString.contains("pseudo=pseudo"));
        assertTrue(toString.contains("bio=bio text"));
        assertTrue(toString.contains("photoProfil=photo.jpg"));
    }
}
