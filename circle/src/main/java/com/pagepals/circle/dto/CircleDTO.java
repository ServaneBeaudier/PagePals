package com.pagepals.circle.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.pagepals.circle.model.ModeRencontre;

import lombok.*;

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

    private List<String> genres;

    private String lieuRencontre;
    private String lienVisio;

    private long createurId;
    private BookDTO livrepropose;

    private boolean isArchived;
}
