package com.pagepals.membership.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pagepals.membership.client.CircleClient;
import com.pagepals.membership.client.UserClient;
import com.pagepals.membership.dto.CircleDTO;
import com.pagepals.membership.dto.ParticipantDTO;
import com.pagepals.membership.exception.AlreadyMemberException;
import com.pagepals.membership.exception.CircleFullException;
import com.pagepals.membership.exception.MembershipNotFoundException;
import com.pagepals.membership.exception.TooLateToRegisterException;
import com.pagepals.membership.model.Membership;
import com.pagepals.membership.model.ModeRencontre;
import com.pagepals.membership.repository.MembershipRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    private final CircleClient circleClient;

    private final UserClient userClient;

    @Override
    @Transactional
    public void inscrire(long userId, long circleId) {
        CircleDTO circle = circleClient.getCircleById(circleId);
        LocalDateTime dateRencontre = circle.getDateRencontre();

        if (dateRencontre == null) {
            throw new IllegalStateException("La date de rencontre n'est pas définie pour ce cercle.");
        }

        if (circle.getCreateurId() == userId) {
            throw new IllegalArgumentException("Le créateur ne peut pas se désinscrire de son cercle.");
        }

        Duration tempsRestant = Duration.between(LocalDateTime.now(), dateRencontre);

        if (circle.getModeRencontre() == ModeRencontre.PRESENTIEL && tempsRestant.toHours() < 24) {
            throw new TooLateToRegisterException(
                    "L'inscription à ce cercle en présentiel est fermée depuis moins de 24h.");
        }

        if (circle.getModeRencontre() == ModeRencontre.ENLIGNE && tempsRestant.toHours() < 12) {
            throw new TooLateToRegisterException(
                    "L'inscription à ce cercle en distanciel est fermée depuis moins de 12h.");
        }

        if (membershipRepository.existsByUserIdAndCircleId(userId, circleId)) {
            throw new AlreadyMemberException("Vous êtes déjà inscrit à ce cercle.");
        }

        long current = membershipRepository.countByCircleId(circleId);
        int max = circleClient.getMaxMembres(circleId);

        if (current >= max) {
            throw new CircleFullException("Ce cercle est complet.");
        }

        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setCircleId(circleId);
        membership.setDateInscription(LocalDateTime.now());

        membershipRepository.save(membership);
    }

    @Override
    public void desinscrire(long userId, long circleId) {
        CircleDTO circle = circleClient.getCircleById(circleId);
        Optional<Membership> existing = membershipRepository.findByUserIdAndCircleId(userId, circleId);
        if (existing.isEmpty()) {
            throw new MembershipNotFoundException("Inscription non trouvée pour cet utilisateur et cercle.");
        }
        if (circle.getCreateurId() == userId) {
            throw new IllegalArgumentException("Le créateur ne peut pas se désinscrire de son cercle.");
        }

        membershipRepository.delete(existing.get());
    }

    @Override
    public List<ParticipantDTO> getParticipantsWithPseudo(long circleId) {
        List<Membership> memberships = membershipRepository.findByCircleId(circleId);
        List<Long> userIds = memberships.stream()
                .map(Membership::getUserId)
                .toList();

        return userClient.getPseudosByIds(userIds);
    }

    @Override
    public boolean estInscrit(long userId, Long circleId) {
        return membershipRepository.existsByUserIdAndCircleId(userId, circleId);

    }

    @Override
    public int countMembersForCircle(Long circleId) {
        return membershipRepository.countByCircleId(circleId);
    }

    @Override
    @Transactional
    public void supprimerToutesLesInscriptionsPourUtilisateur(Long userId) {
        List<Membership> inscriptions = membershipRepository.findByUserId(userId);
        membershipRepository.deleteAll(inscriptions);
    }
}
