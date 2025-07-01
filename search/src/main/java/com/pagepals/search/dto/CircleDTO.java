package com.pagepals.search.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CircleDTO {

    private long id;

    private String nom;

    private String description;

    private String modeRencontre;

    private String lieuRencontre;

    private String lienVisio;

    private LocalDate dateRencontre;

    private List<String> genres;

    private Integer nbMaxMembres;

    private int nombreInscrits;

    private boolean estOuvert;
}
