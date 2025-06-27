package com.pagepals.circle.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pagepals.circle.model.ModeRencontre;

import lombok.Data;

@Data
public class CircleDTO {

    private String nom;
    private String description;
    private ModeRencontre modeRencontre;
    private LocalDate dateCreation;
    private LocalDateTime dateRencontre;

    private String lieuRencontre;
    private String lienVisio;

    private long createurId;
    private BookDTO livrepropose;

    private boolean isArchived;
}
