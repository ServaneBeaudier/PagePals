package com.pagepals.circle.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="circle")
public class Circle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModeRencontre modeRencontre;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Column(nullable = false)
    private LocalDateTime dateRencontre;

    private String lieuRencontre;

    private String lienVisio;

    @Column(nullable = false)
    private long createurId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="livre_propose_id")
    private Book livrePropose;

    private boolean isArchived;

}
