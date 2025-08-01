package com.pagepals.membership.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDTO {

    private long userId;

    private long circleId;

    private LocalDateTime dateInscription;
}
