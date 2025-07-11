package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.UserProfileDTO;

public class UserProfileDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setPseudo("johnDoe");
        dto.setDateInscription(LocalDate.of(2023, 7, 15));
        dto.setBio("Bio here");
        dto.setPhotoProfil("photo.png");

        assertEquals("johnDoe", dto.getPseudo());
        assertEquals(LocalDate.of(2023, 7, 15), dto.getDateInscription());
        assertEquals("Bio here", dto.getBio());
        assertEquals("photo.png", dto.getPhotoProfil());
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfileDTO dto1 = new UserProfileDTO();
        dto1.setPseudo("john");
        dto1.setDateInscription(LocalDate.of(2023, 7, 15));
        dto1.setBio("Bio1");
        dto1.setPhotoProfil("pic1.png");

        UserProfileDTO dto2 = new UserProfileDTO();
        dto2.setPseudo("john");
        dto2.setDateInscription(LocalDate.of(2023, 7, 15));
        dto2.setBio("Bio1");
        dto2.setPhotoProfil("pic1.png");

        UserProfileDTO dto3 = new UserProfileDTO();
        dto3.setPseudo("jane");
        dto3.setDateInscription(LocalDate.of(2024, 1, 1));
        dto3.setBio("Bio2");
        dto3.setPhotoProfil("pic2.png");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setPseudo("alice");
        dto.setDateInscription(LocalDate.of(2023, 7, 15));
        dto.setBio("Test bio");
        dto.setPhotoProfil("alice.png");

        String str = dto.toString();

        assertTrue(str.contains("pseudo=alice"));
        assertTrue(str.contains("dateInscription=2023-07-15"));
        assertTrue(str.contains("bio=Test bio"));
        assertTrue(str.contains("photoProfil=alice.png"));
    }
}
