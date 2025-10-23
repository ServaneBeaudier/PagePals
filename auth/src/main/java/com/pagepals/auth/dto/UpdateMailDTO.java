package com.pagepals.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Données nécessaires à la mise à jour de l'adresse e-mail d'un utilisateur.
 * Transmises au service d'authentification pour modifier l'information correspondante
 * dans la base de données.
 */
@Data
public class UpdateMailDTO {

    private long userId;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String newEmail;

}
