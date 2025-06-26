package com.pagepals.auth.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileCreateRequest {

    private long id;

    private LocalDate dateInscription;
}
