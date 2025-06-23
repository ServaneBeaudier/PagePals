package com.pagepals.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileDTO {

    @NotBlank(message = "Le pseudo est obligatoire")
    @Size(min = 3, max = 30, message = "Le pseudo doit contenir entre 3 et 30 caractères")
    private String pseudo;

    @Size(min = 0, max = 1000)
    private String bio;

    private String photoProfil;
}
