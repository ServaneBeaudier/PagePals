package com.pagepals.user.modeltest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.pagepals.user.model.UserProfile;

public class UserProfileTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo("testUser");
        user.setBio("Ma bio");
        user.setPhotoProfil("photo.jpg");
        user.setDateInscription(LocalDate.of(2023, 7, 15));

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getPseudo());
        assertEquals("Ma bio", user.getBio());
        assertEquals("photo.jpg", user.getPhotoProfil());
        assertEquals(LocalDate.of(2023, 7, 15), user.getDateInscription());
    }

    @Test
    void testAllArgsConstructor() {
        UserProfile user = new UserProfile(
                2L,
                "user2",
                "Bio user2",
                "pic.png",
                LocalDate.of(2024, 1, 1));

        assertEquals(2L, user.getId());
        assertEquals("user2", user.getPseudo());
        assertEquals("Bio user2", user.getBio());
        assertEquals("pic.png", user.getPhotoProfil());
        assertEquals(LocalDate.of(2024, 1, 1), user.getDateInscription());
    }

    @Test
    void testBuilder() {
        UserProfile user = UserProfile.builder()
                .id(3L)
                .pseudo("builderUser")
                .bio("Builder bio")
                .photoProfil("builder.png")
                .dateInscription(LocalDate.of(2025, 5, 5))
                .build();

        assertEquals(3L, user.getId());
        assertEquals("builderUser", user.getPseudo());
        assertEquals("Builder bio", user.getBio());
        assertEquals("builder.png", user.getPhotoProfil());
        assertEquals(LocalDate.of(2025, 5, 5), user.getDateInscription());
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfile user1 = UserProfile.builder()
                .id(1L)
                .pseudo("user")
                .bio("bio")
                .photoProfil("pic.png")
                .dateInscription(LocalDate.of(2023, 7, 15))
                .build();

        UserProfile user2 = UserProfile.builder()
                .id(1L)
                .pseudo("user")
                .bio("bio")
                .photoProfil("pic.png")
                .dateInscription(LocalDate.of(2023, 7, 15))
                .build();

        UserProfile user3 = UserProfile.builder()
                .id(2L)
                .pseudo("user3")
                .bio("bio3")
                .photoProfil("pic3.png")
                .dateInscription(LocalDate.of(2024, 1, 1))
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
    }

    @Test
    void testToString() {
        UserProfile user = UserProfile.builder()
                .id(5L)
                .pseudo("toStringUser")
                .bio("Test toString")
                .photoProfil("tos.png")
                .dateInscription(LocalDate.of(2023, 7, 15))
                .build();

        String str = user.toString();

        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("pseudo=toStringUser"));
        assertTrue(str.contains("bio=Test toString"));
        assertTrue(str.contains("photoProfil=tos.png"));
        assertTrue(str.contains("dateInscription=2023-07-15"));
    }
}
