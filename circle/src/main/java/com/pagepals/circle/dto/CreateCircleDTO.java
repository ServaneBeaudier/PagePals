package com.pagepals.circle.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateCircleDTO {

    private String nom;
    private String description;
    private String modeRencontre;
    private LocalDateTime dateRencontre;

    private String lieuRencontre;
    private String lienVisio;

    private long createurId;
    private BookDTO livrepropose;
}
