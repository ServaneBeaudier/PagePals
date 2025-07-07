package com.pagepals.user.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private Long userId;
    private String pseudo;
    private String photoProfil;
    private LocalDate dateInscription;
    private String bio;
}
