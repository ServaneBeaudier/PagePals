package com.pagepals.circle.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.pagepals.circle.model.ModeRencontre;

import lombok.Data;

@Data
public class CreateCircleDTO {

    private String nom;
    private String description;
    private ModeRencontre modeRencontre;
    private LocalDateTime dateRencontre;

    private Integer nbMaxMembres;

    private List<Long> genreIds;

    private String lieuRencontre;
    private AdresseDetailsDTO lieuRencontreDetails;
    private String lienVisio;

    private long createurId;
    private BookDTO livrePropose;
}
