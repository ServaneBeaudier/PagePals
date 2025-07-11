package com.pagepals.auth.modeltest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.auth.model.Role;
import com.pagepals.auth.model.UserEntity;

class UserEntityTest {

    @Test
    void testGettersSetters() {
        UserEntity user = new UserEntity();

        user.setId(1L);
        assertEquals(1L, user.getId());

        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());

        user.setMotDePasse("password123");
        assertEquals("password123", user.getMotDePasse());

        user.setRole(Role.MEMBRE);
        assertEquals(Role.MEMBRE, user.getRole());
    }

    @Test
    void testBuilder() {
        UserEntity user = UserEntity.builder()
            .id(2L)
            .email("builder@example.com")
            .motDePasse("builderpass")
            .role(Role.ADMIN)
            .build();

        assertEquals(2L, user.getId());
        assertEquals("builder@example.com", user.getEmail());
        assertEquals("builderpass", user.getMotDePasse());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        UserEntity user1 = UserEntity.builder()
            .id(1L)
            .email("user@example.com")
            .motDePasse("pass")
            .role(Role.MEMBRE)
            .build();

        UserEntity user2 = UserEntity.builder()
            .id(1L)
            .email("user@example.com")
            .motDePasse("pass")
            .role(Role.MEMBRE)
            .build();

        UserEntity user3 = UserEntity.builder()
            .id(2L)
            .email("diff@example.com")
            .motDePasse("pass")
            .role(Role.ADMIN)
            .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        UserEntity user = UserEntity.builder()
            .id(1L)
            .email("user@example.com")
            .motDePasse("pass")
            .role(Role.MEMBRE)
            .build();

        String s = user.toString();
        assertTrue(s.contains("user@example.com"));
        assertTrue(s.contains("MEMBRE"));
    }
}
