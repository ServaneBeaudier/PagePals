package com.pagepals.circle.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UpdateCircleDTO {

    private String nom;
    private String description;
    private String modeRencontre;
    private LocalDateTime dateRencontre;

    private String lieuRencontre;
    private String lienVisio;

    private BookDTO livrepropose;
}
