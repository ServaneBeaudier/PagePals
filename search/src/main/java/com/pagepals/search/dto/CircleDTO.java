package com.pagepals.search.dto;

import java.time.LocalDate;

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

    private String lienVision;

    private LocalDate dateRencontre;

    private String genre;
}
