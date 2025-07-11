package com.pagepals.membership.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pagepals.membership.dto.MembershipDTO;

public class MembershipDTOTest {

    @Test
    void testGettersSetters() {
        MembershipDTO dto = new MembershipDTO();
        dto.setUserId(101L);
        dto.setCircleId(202L);
        LocalDateTime now = LocalDateTime.now();
        dto.setDateInscription(now);

        assertEquals(101L, dto.getUserId());
        assertEquals(202L, dto.getCircleId());
        assertEquals(now, dto.getDateInscription());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.of(2025, 7, 12, 15, 30);
        MembershipDTO dto = new MembershipDTO(1L, 2L, date);

        assertEquals(1L, dto.getUserId());
        assertEquals(2L, dto.getCircleId());
        assertEquals(date, dto.getDateInscription());
    }

    @Test
    void testEqualsHashCodeAndToString() {
        MembershipDTO dto1 = new MembershipDTO(1L, 2L, LocalDateTime.now());
        MembershipDTO dto2 = new MembershipDTO(1L, 2L, dto1.getDateInscription());

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("userId"));
    }
}
