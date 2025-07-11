package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.UserInfoDTO;

public class UserInfoDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        UserInfoDTO dto = new UserInfoDTO();

        dto.setUserId(42L);
        dto.setPseudo("userPseudo");
        dto.setPhotoProfil("photo.png");
        dto.setDateInscription(LocalDate.of(2023, 7, 11));
        dto.setBio("Ma bio");

        assertEquals(42L, dto.getUserId());
        assertEquals("userPseudo", dto.getPseudo());
        assertEquals("photo.png", dto.getPhotoProfil());
        assertEquals(LocalDate.of(2023, 7, 11), dto.getDateInscription());
        assertEquals("Ma bio", dto.getBio());
    }

    @Test
    void testEqualsAndHashCode() {
        UserInfoDTO dto1 = new UserInfoDTO(1L, "pseudo", "photo", LocalDate.of(2023,1,1), "bio");
        UserInfoDTO dto2 = new UserInfoDTO(1L, "pseudo", "photo", LocalDate.of(2023,1,1), "bio");
        UserInfoDTO dto3 = new UserInfoDTO(2L, "pseudo2", "photo2", LocalDate.of(2023,2,2), "bio2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        UserInfoDTO dto = new UserInfoDTO(5L, "pseudo", "photo.jpg", LocalDate.of(2023,7,11), "bio text");

        String str = dto.toString();

        assertTrue(str.contains("userId=5"));
        assertTrue(str.contains("pseudo=pseudo"));
        assertTrue(str.contains("photoProfil=photo.jpg"));
        assertTrue(str.contains("dateInscription=2023-07-11"));
        assertTrue(str.contains("bio=bio text"));
    }
}
