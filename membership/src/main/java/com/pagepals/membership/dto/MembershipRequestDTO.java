package com.pagepals.membership.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MembershipRequestDTO {

    private long userId;

    private long circleId;

    private LocalDateTime dateInscription;

}
