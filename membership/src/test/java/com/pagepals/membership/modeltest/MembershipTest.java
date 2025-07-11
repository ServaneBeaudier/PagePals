package com.pagepals.membership.modeltest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pagepals.membership.model.Membership;

public class MembershipTest {

    @Test
    void testGettersSetters() {
        Membership membership = new Membership();
        LocalDateTime now = LocalDateTime.now();

        membership.setId(1L);
        membership.setUserId(2L);
        membership.setCircleId(3L);
        membership.setDateInscription(now);

        assertEquals(1L, membership.getId());
        assertEquals(2L, membership.getUserId());
        assertEquals(3L, membership.getCircleId());
        assertEquals(now, membership.getDateInscription());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();

        Membership membership = Membership.builder()
            .id(1L)
            .userId(2L)
            .circleId(3L)
            .dateInscription(now)
            .build();

        assertEquals(1L, membership.getId());
        assertEquals(2L, membership.getUserId());
        assertEquals(3L, membership.getCircleId());
        assertEquals(now, membership.getDateInscription());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        Membership m1 = Membership.builder()
            .id(1L)
            .userId(2L)
            .circleId(3L)
            .dateInscription(now)
            .build();

        Membership m2 = Membership.builder()
            .id(1L)
            .userId(2L)
            .circleId(3L)
            .dateInscription(now)
            .build();

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testToString() {
        Membership membership = new Membership();
        membership.setId(1L);
        membership.setUserId(2L);
        membership.setCircleId(3L);
        membership.setDateInscription(LocalDateTime.of(2025, 7, 11, 12, 0));

        String str = membership.toString();

        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("userId=2"));
        assertTrue(str.contains("circleId=3"));
        assertTrue(str.contains("dateInscription=2025-07-11T12:00"));
    }
}
