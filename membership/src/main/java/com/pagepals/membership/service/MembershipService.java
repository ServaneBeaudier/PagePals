package com.pagepals.membership.service;

import java.util.List;

import com.pagepals.membership.dto.ParticipantDTO;

public interface MembershipService {

    void inscrire(long userId, long circleId);

    void desinscrire(long userId, long circleId);

    List<ParticipantDTO> getParticipantsWithPseudo(long circleId);

    boolean estInscrit(long userId, Long circleId);

    int countMembersForCircle(Long circleId);

    void supprimerToutesLesInscriptionsPourUtilisateur(Long userId);
}
