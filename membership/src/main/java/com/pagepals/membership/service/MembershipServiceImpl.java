package com.pagepals.membership.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pagepals.membership.client.*;
import com.pagepals.membership.dto.*;
import com.pagepals.membership.exception.*;
import com.pagepals.membership.model.*;
import com.pagepals.membership.repository.MembershipRepository;

import feign.FeignException;
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
        CircleDTO circle = null;
        int attempts = 0;
        while (circle == null && attempts < 3) {
            try {
                circle = circleClient.getCircleById(circleId);
            } catch (FeignException.NotFound e) {
                attempts++;
                try {
                    Thread.sleep(500); // 0.5 seconde d’attente
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (circle == null) {
            throw new IllegalStateException("Cercle non trouvé après plusieurs tentatives");
        }
        LocalDateTime dateRencontre = circle.getDateRencontre();

        if (dateRencontre == null) {
            throw new IllegalStateException("La date de rencontre n'est pas définie pour ce cercle.");
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
        List<Long> userIds = new ArrayList<>(memberships.stream()
                .map(Membership::getUserId)
                .toList());

        CircleDTO circle = circleClient.getCircleById(circleId);
        if (circle.getCreateurId() != null && !userIds.contains(circle.getCreateurId())) {
            userIds.add(circle.getCreateurId());
        }

        return userClient.getPseudosByIds(userIds);
    }

    @Override
    public boolean estInscrit(long userId, Long circleId) {
        CircleDTO circle = circleClient.getCircleById(circleId);
        if (circle.getCreateurId() == userId) {
            return true;
        }
        return membershipRepository.existsByUserIdAndCircleId(userId, circleId);
    }

    @Override
    public int countMembersForCircle(Long circleId) {
        return membershipRepository.countByCircleId(circleId)+1;
    }

    @Override
    @Transactional
    public void supprimerToutesLesInscriptionsPourUtilisateur(Long userId) {
        List<Membership> inscriptions = membershipRepository.findByUserId(userId);
        membershipRepository.deleteAll(inscriptions);
    }

    public List<CircleDTO> findActiveCirclesJoinedByUser(Long userId) {
        List<Membership> memberships = membershipRepository.findByUserId(userId);

        List<Long> joinedCircleIds = memberships.stream()
                .map(Membership::getCircleId)
                .collect(Collectors.toList());

        List<CircleDTO> activeCircles = circleClient.getCirclesActive();

        return activeCircles.stream()
                .filter(circle -> joinedCircleIds.contains(circle.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CircleDTO> findActiveCirclesCreatedByUser(Long userId) {
        List<CircleDTO> activeCircles = circleClient.getCirclesActive();

        return activeCircles.stream()
                .filter(circle -> userId.equals(circle.getCreateurId()))
                .collect(Collectors.toList());
    }

}
