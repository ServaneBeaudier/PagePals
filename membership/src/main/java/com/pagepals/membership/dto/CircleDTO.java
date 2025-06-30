package com.pagepals.membership.dto;

import java.time.LocalDateTime;

import com.pagepals.membership.model.ModeRencontre;

import lombok.Data;

@Data
public class CircleDTO {
    private long id;
    private String nom;
    private String description;
    private LocalDateTime dateRencontre;
    private ModeRencontre modeRencontre;
    private long createurId;
}
