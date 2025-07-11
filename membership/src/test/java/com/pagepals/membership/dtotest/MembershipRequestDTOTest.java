package com.pagepals.membership.dtotest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pagepals.membership.dto.MembershipRequestDTO;

public class MembershipRequestDTOTest {

    @Test
    void testGettersSetters() {
        MembershipRequestDTO dto = new MembershipRequestDTO();
        dto.setUserId(111L);
        dto.setCircleId(222L);
        LocalDateTime now = LocalDateTime.now();
        dto.setDateInscription(now);

        assertEquals(111L, dto.getUserId());
        assertEquals(222L, dto.getCircleId());
        assertEquals(now, dto.getDateInscription());
    }

    @Test
    void testEqualsHashCodeAndToString() {
        MembershipRequestDTO dto1 = new MembershipRequestDTO();
        dto1.setUserId(1L);
        dto1.setCircleId(2L);
        dto1.setDateInscription(LocalDateTime.now());

        MembershipRequestDTO dto2 = new MembershipRequestDTO();
        dto2.setUserId(1L);
        dto2.setCircleId(2L);
        dto2.setDateInscription(dto1.getDateInscription());

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("userId"));
    }
}
