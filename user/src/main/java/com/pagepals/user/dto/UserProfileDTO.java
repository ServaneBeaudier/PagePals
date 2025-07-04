package com.pagepals.user.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileDTO {

    private String pseudo;

    private LocalDate dateInscription;

    private String bio;

    private String photoProfil;
}
