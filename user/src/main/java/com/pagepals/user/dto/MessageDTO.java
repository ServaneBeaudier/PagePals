package com.pagepals.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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