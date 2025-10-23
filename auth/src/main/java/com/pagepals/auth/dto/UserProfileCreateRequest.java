package com.pagepals.auth.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * Requête envoyée depuis le service d'authentification vers le service utilisateur
 * pour créer automatiquement le profil associé à un nouvel utilisateur inscrit.
 */
@Data
public class UserProfileCreateRequest {

    private long id;

    private LocalDate dateInscription;

    private String pseudo;
}
