package com.pagepals.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Données reçues lors de l'inscription d'un nouvel utilisateur.
 * Contient les informations de base nécessaires à la création du compte.
 * 
 * Les contraintes de validation garantissent la cohérence et la sécurité des données saisies.
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 12, message = "Le mot de passe doit contenir entre 8 et 12 caractères")
    private String motDePasse;

    @NotBlank(message = "Le pseudo est obligatoire")
    @Size(min = 3, max = 30, message = "Le pseudo doit contenir entre 3 et 30 caractères")
    private String pseudo;
}
