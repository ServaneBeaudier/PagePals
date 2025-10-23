package com.pagepals.circle.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un cercle littéraire.
 * 
 * Un cercle regroupe plusieurs membres autour d’un thème, d’un genre ou d’un livre.
 * Il peut se dérouler en présentiel ou en ligne, et dispose d’un créateur, d’une date de rencontre,
 * et éventuellement d’un livre proposé pour la discussion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "circle")
public class Circle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private AdresseDetails lieuRencontreDetails;

    private String lienVisio;

    @Column(name = "nb_max_membres")
    private Integer nbMaxMembres;

    @Column(name = "nb_inscrits")
    private Integer memberCounts;

    @Column(nullable = false)
    private Long createurId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "livre_propose_id")
    private Book livrePropose;

    private boolean isArchived;

    @ManyToMany
    @JoinTable(name = "circle_genre", joinColumns = @JoinColumn(name = "circle_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<LiteraryGenre> genres = new HashSet<>();
}
