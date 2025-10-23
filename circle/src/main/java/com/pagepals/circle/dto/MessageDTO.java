package com.pagepals.circle.dto;

import java.time.LocalDateTime;

import lombok.*;


/**
 * Représente un message échangé dans un cercle littéraire.
 * 
 * Contient les informations relatives à son auteur, son contenu,
 * sa date d'envoi et le cercle auquel il est rattaché.
 */
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