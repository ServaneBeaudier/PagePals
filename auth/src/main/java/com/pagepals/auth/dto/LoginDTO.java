package com.pagepals.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Représente les informations envoyées par l'utilisateur lors de la tentative de connexion.
 * 
 * Les champs sont validés côté serveur via les annotations Jakarta Validation.
 */
@Data
public class LoginDTO {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

}
