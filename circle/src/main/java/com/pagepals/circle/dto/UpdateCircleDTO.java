package com.pagepals.circle.dto;

import java.time.LocalDateTime;

import com.pagepals.circle.model.ModeRencontre;

import lombok.Data;

@Data
public class UpdateCircleDTO {

    private long id;

    private String nom;
    private String description;
    private ModeRencontre modeRencontre;
    private LocalDateTime dateRencontre;

    private String lieuRencontre;
    private String lienVisio;

    private BookDTO livrePropose;
}
