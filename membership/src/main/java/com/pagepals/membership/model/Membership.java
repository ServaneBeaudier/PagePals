package com.pagepals.membership.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "membership", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "circle_id"})
})
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "circle_id", nullable = false)
    private long circleId;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

}
