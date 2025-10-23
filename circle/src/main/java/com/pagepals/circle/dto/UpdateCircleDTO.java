package com.pagepals.circle.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.pagepals.circle.model.ModeRencontre;

import lombok.Data;

/**
 * Données utilisées pour la mise à jour d'un cercle littéraire existant.
 * 
 * Permet de modifier les informations principales d'un cercle, comme son nom,
 * sa date de rencontre, son mode, son lieu ou le livre proposé.
 */
@Data
public class UpdateCircleDTO {

    private long id;

    private String nom;
    private String description;
    private ModeRencontre modeRencontre;
    private LocalDateTime dateRencontre;

    private Integer nbMaxMembres;

    private List<Long> genreIds;

    private String lieuRencontre;
    private AdresseDetailsDTO lieuRencontreDetails;
    private String lienVisio;

    private BookDTO livrePropose;
}
