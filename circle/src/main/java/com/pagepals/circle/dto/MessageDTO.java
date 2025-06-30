package com.pagepals.circle.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {

    private long id;

    private String contenu;

    private Long auteurId;

    private String pseudoAuteur;

    private String photoAuteur;

    private LocalDateTime dateEnvoi;

    private long circleId;
}