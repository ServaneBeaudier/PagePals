package com.pagepals.membership.dto;

import java.time.LocalDateTime;

import com.pagepals.membership.model.ModeRencontre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CircleDTO {
    private long id;
    private String nom;
    private String description;
    private LocalDateTime dateRencontre;
    private ModeRencontre modeRencontre;
    private Long createurId;
    private Integer nbMaxMembres;
    private Integer membersCount;
}
