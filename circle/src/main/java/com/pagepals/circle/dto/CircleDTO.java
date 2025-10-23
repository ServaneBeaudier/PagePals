package com.pagepals.circle.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.pagepals.circle.model.ModeRencontre;

import lombok.*;

/**
 * Représente un cercle littéraire tel qu'il est exposé aux autres microservices ou au front-end.
 * 
 * Contient les informations principales du cercle : identité, organisation,
 * modes de rencontre, livre choisi, créateur et état d'archivage.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CircleDTO {

    private long id;

    private String nom;
    private String description;
    private ModeRencontre modeRencontre;
    private LocalDate dateCreation;
    private LocalDateTime dateRencontre;

    private Integer nbMaxMembres;
    private Integer membersCount;

    private List<String> genres;
    private List<Long> genreIds;

    private String lieuRencontre;
    private AdresseDetailsDTO lieuRencontreDetails;
    private String lienVisio;

    private Long createurId;
    private BookDTO livrePropose;

    private boolean isArchived;
}
