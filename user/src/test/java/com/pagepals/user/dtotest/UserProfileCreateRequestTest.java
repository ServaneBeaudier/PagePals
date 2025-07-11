package com.pagepals.user.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.pagepals.user.dto.UserProfileCreateRequest;

public class UserProfileCreateRequestTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        UserProfileCreateRequest dto = new UserProfileCreateRequest();

        dto.setId(10L);
        dto.setPseudo("testUser");

        assertEquals(10L, dto.getId());
        assertEquals("testUser", dto.getPseudo());
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfileCreateRequest dto1 = new UserProfileCreateRequest();
        dto1.setId(1L);
        dto1.setPseudo("pseudo");

        UserProfileCreateRequest dto2 = new UserProfileCreateRequest();
        dto2.setId(1L);
        dto2.setPseudo("pseudo");

        UserProfileCreateRequest dto3 = new UserProfileCreateRequest();
        dto3.setId(2L);
        dto3.setPseudo("pseudo2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        UserProfileCreateRequest dto = new UserProfileCreateRequest();
        dto.setId(5L);
        dto.setPseudo("userTest");

        String str = dto.toString();

        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("pseudo=userTest"));
    }
}
