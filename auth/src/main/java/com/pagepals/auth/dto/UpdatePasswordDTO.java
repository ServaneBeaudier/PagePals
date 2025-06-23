package com.pagepals.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDTO {

    private long userId;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 12, message = "Le mot de passe doit contenir entre 8 et 12 caractères")
    private String newMotDePasse;
}
