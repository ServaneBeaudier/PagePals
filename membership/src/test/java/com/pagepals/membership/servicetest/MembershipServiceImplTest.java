package com.pagepals.membership.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pagepals.membership.client.*;
import com.pagepals.membership.dto.*;
import com.pagepals.membership.exception.*;
import com.pagepals.membership.model.*;
import com.pagepals.membership.repository.MembershipRepository;
import com.pagepals.membership.service.MembershipServiceImpl;

import feign.FeignException;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceImplTest {

    @Mock
    MembershipRepository membershipRepository;

    @Mock
    CircleClient circleClient;

    @Mock
    UserClient userClient;

    @InjectMocks
    MembershipServiceImpl membershipService;

    private final long userId = 1L;
    private final long circleId = 2L;

    private CircleDTO createCircleDTO(ModeRencontre mode, LocalDateTime dateRencontre) {
        CircleDTO c = new CircleDTO();
        c.setModeRencontre(mode);
        c.setDateRencontre(dateRencontre);
        return c;
    }

    @Test
    void inscrire_success() {
        CircleDTO circle = createCircleDTO(ModeRencontre.PRESENTIEL, LocalDateTime.now().plusDays(2));

        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(membershipRepository.existsByUserIdAndCircleId(userId, circleId)).thenReturn(false);
        when(membershipRepository.countByCircleId(circleId)).thenReturn((int) 5L);
        when(circleClient.getMaxMembres(circleId)).thenReturn(10);

        membershipService.inscrire(userId, circleId);

        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(captor.capture());

        Membership saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(circleId, saved.getCircleId());
        assertNotNull(saved.getDateInscription());
    }

    @Test
    void inscrire_cercleIntrouvable_retriesThenFail() {
        FeignException.NotFound feignNotFound = mock(FeignException.NotFound.class);
        AtomicInteger callCount = new AtomicInteger(0);

        doAnswer(invocation -> {
            callCount.incrementAndGet();
            throw feignNotFound;
        }).when(circleClient).getCircleById(circleId);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> membershipService.inscrire(userId, circleId));
        assertEquals("Cercle non trouvé après plusieurs tentatives", ex.getMessage());
        assertEquals(3, callCount.get());
    }

    @Test
    void inscrire_dateRencontreNull_throws() {
        CircleDTO circle = createCircleDTO(ModeRencontre.PRESENTIEL, null);
        when(circleClient.getCircleById(circleId)).thenReturn(circle);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> membershipService.inscrire(userId, circleId));
        assertEquals("La date de rencontre n'est pas définie pour ce cercle.", ex.getMessage());
    }

    @Test
    void inscrire_tooLatePresentiel_throws() {
        CircleDTO circle = createCircleDTO(ModeRencontre.PRESENTIEL, LocalDateTime.now().plusHours(23));
        when(circleClient.getCircleById(circleId)).thenReturn(circle);

        TooLateToRegisterException ex = assertThrows(TooLateToRegisterException.class,
                () -> membershipService.inscrire(userId, circleId));
        assertEquals("L'inscription à ce cercle en présentiel est fermée depuis moins de 24h.", ex.getMessage());
    }

    @Test
    void inscrire_tooLateEnLigne_throws() {
        CircleDTO circle = createCircleDTO(ModeRencontre.ENLIGNE, LocalDateTime.now().plusHours(11));
        when(circleClient.getCircleById(circleId)).thenReturn(circle);

        TooLateToRegisterException ex = assertThrows(TooLateToRegisterException.class,
                () -> membershipService.inscrire(userId, circleId));
        assertEquals("L'inscription à ce cercle en distanciel est fermée depuis moins de 12h.", ex.getMessage());
    }

    @Test
    void inscrire_alreadyMember_throws() {
        CircleDTO circle = createCircleDTO(ModeRencontre.PRESENTIEL, LocalDateTime.now().plusDays(2));

        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(membershipRepository.existsByUserIdAndCircleId(userId, circleId)).thenReturn(true);

        AlreadyMemberException ex = assertThrows(AlreadyMemberException.class,
                () -> membershipService.inscrire(userId, circleId));
        assertEquals("Vous êtes déjà inscrit à ce cercle.", ex.getMessage());
    }

    @Test
    void inscrire_cercleFull_throws() {
        CircleDTO circle = createCircleDTO(ModeRencontre.PRESENTIEL, LocalDateTime.now().plusDays(2));

        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(membershipRepository.existsByUserIdAndCircleId(userId, circleId)).thenReturn(false);
        when(membershipRepository.countByCircleId(circleId)).thenReturn((int) 10L);
        when(circleClient.getMaxMembres(circleId)).thenReturn(10);

        CircleFullException ex = assertThrows(CircleFullException.class,
                () -> membershipService.inscrire(userId, circleId));
        assertEquals("Ce cercle est complet.", ex.getMessage());
    }

    @Test
    void desinscrire_success() {
        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(2L);

        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setCircleId(circleId);

        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(membershipRepository.findByUserIdAndCircleId(userId, circleId))
                .thenReturn(Optional.of(membership));

        membershipService.desinscrire(userId, circleId);

        verify(membershipRepository).delete(membership);
    }

    @Test
    void desinscrire_membershipNotFound_throws() {
        when(membershipRepository.findByUserIdAndCircleId(userId, circleId))
                .thenReturn(Optional.empty());

        when(circleClient.getCircleById(circleId)).thenReturn(new CircleDTO());

        assertThrows(MembershipNotFoundException.class, () -> membershipService.desinscrire(userId, circleId));
    }

    @Test
    void desinscrire_creatorCannotUnsubscribe_throws() {
        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(userId);

        when(circleClient.getCircleById(circleId)).thenReturn(circle);

        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setCircleId(circleId);

        when(membershipRepository.findByUserIdAndCircleId(userId, circleId))
                .thenReturn(Optional.of(membership));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> membershipService.desinscrire(userId, circleId));
        assertEquals("Le créateur ne peut pas se désinscrire de son cercle.", ex.getMessage());
    }

    @Test
    void getParticipantsWithPseudo_includesCreator() {
        Long circleId = 1L;
        Membership m1 = new Membership();
        m1.setUserId(2L);
        Membership m2 = new Membership();
        m2.setUserId(3L);
        List<Membership> memberships = List.of(m1, m2);

        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(4L);

        List<ParticipantDTO> participants = List.of(
                new ParticipantDTO(2L, "user2", null),
                new ParticipantDTO(3L, "user3", null),
                new ParticipantDTO(4L, "creator", null));

        when(membershipRepository.findByCircleId(circleId)).thenReturn(memberships);
        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(userClient.getPseudosByIds(Arrays.asList(2L, 3L, 4L))).thenReturn(participants);

        List<ParticipantDTO> result = membershipService.getParticipantsWithPseudo(circleId);

        assertEquals(3, result.size());
        verify(userClient).getPseudosByIds(Arrays.asList(2L, 3L, 4L));
    }

    @Test
    void getParticipantsWithPseudo_creatorAlreadyInList() {
        Long circleId = 1L;
        Membership m1 = new Membership();
        m1.setUserId(2L);
        Membership m2 = new Membership();
        m2.setUserId(3L);
        Membership m3 = new Membership();
        m3.setUserId(4L);
        List<Membership> memberships = List.of(m1, m2, m3);

        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(4L);

        List<ParticipantDTO> participants = List.of(
                new ParticipantDTO(2L, "user2", null),
                new ParticipantDTO(3L, "user3", null),
                new ParticipantDTO(4L, "creator", null));

        when(membershipRepository.findByCircleId(circleId)).thenReturn(memberships);
        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(userClient.getPseudosByIds(Arrays.asList(2L, 3L, 4L))).thenReturn(participants);

        List<ParticipantDTO> result = membershipService.getParticipantsWithPseudo(circleId);

        assertEquals(3, result.size());
        verify(userClient).getPseudosByIds(Arrays.asList(2L, 3L, 4L));
    }

    @Test
    void estInscrit_userIsCreator_returnsTrue() {
        Long userId = 1L;
        Long circleId = 10L;

        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(userId);

        when(circleClient.getCircleById(circleId)).thenReturn(circle);

        boolean result = membershipService.estInscrit(userId, circleId);

        assertTrue(result);
        verify(membershipRepository, never()).existsByUserIdAndCircleId(anyLong(), anyLong());
    }

    @Test
    void estInscrit_userNotCreator_existsInMembership_returnsTrue() {
        Long userId = 2L;
        Long circleId = 10L;

        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(1L);

        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(membershipRepository.existsByUserIdAndCircleId(userId, circleId)).thenReturn(true);

        boolean result = membershipService.estInscrit(userId, circleId);

        assertTrue(result);
    }

    @Test
    void estInscrit_userNotCreator_notExistsInMembership_returnsFalse() {
        Long userId = 2L;
        Long circleId = 10L;

        CircleDTO circle = new CircleDTO();
        circle.setCreateurId(1L);

        when(circleClient.getCircleById(circleId)).thenReturn(circle);
        when(membershipRepository.existsByUserIdAndCircleId(userId, circleId)).thenReturn(false);

        boolean result = membershipService.estInscrit(userId, circleId);

        assertFalse(result);
    }

    @Test
    void countMembersForCircle_returnsCountPlusOne() {
        Long circleId = 5L;
        when(membershipRepository.countByCircleId(circleId)).thenReturn(3);

        int result = membershipService.countMembersForCircle(circleId);

        assertEquals(4, result); // 3 + 1 = 4
        verify(membershipRepository, times(1)).countByCircleId(circleId);
    }

    @Test
    void supprimerToutesLesInscriptionsPourUtilisateur_existingUser_deletesAll() {
        Long userId = 5L;
        List<Membership> inscriptions = List.of(
                new Membership(1L, userId, 100L, LocalDateTime.now()),
                new Membership(2L, userId, 101L, LocalDateTime.now()));
        when(membershipRepository.findByUserId(userId)).thenReturn(inscriptions);

        membershipService.supprimerToutesLesInscriptionsPourUtilisateur(userId);

        verify(membershipRepository).deleteAll(inscriptions);
    }

    @Test
    void findActiveCirclesJoinedByUser_existingMemberships_returnsFilteredCircles() {
        Long userId = 10L;

        Membership m1 = new Membership();
        m1.setCircleId(1L);
        Membership m2 = new Membership();
        m2.setCircleId(3L);
        List<Membership> memberships = List.of(m1, m2);

        when(membershipRepository.findByUserId(userId)).thenReturn(memberships);

        CircleDTO circle1 = CircleDTO.builder().id(1L).nom("Circle 1").build();
        CircleDTO circle2 = CircleDTO.builder().id(2L).nom("Circle 2").build();
        CircleDTO circle3 = CircleDTO.builder().id(3L).nom("Circle 3").build();
        List<CircleDTO> activeCircles = List.of(circle1, circle2, circle3);

        when(circleClient.getCirclesActive()).thenReturn(activeCircles);

        List<CircleDTO> result = membershipService.findActiveCirclesJoinedByUser(userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId() == 1L));
        assertTrue(result.stream().anyMatch(c -> c.getId() == 3L));
    }

    @Test
    void findActiveCirclesCreatedByUser_existingActiveCircles_returnsFilteredCircles() {
        Long userId = 10L;

        CircleDTO circle1 = CircleDTO.builder().id(1L).createurId(userId).nom("Circle 1").build();
        CircleDTO circle2 = CircleDTO.builder().id(2L).createurId(99L).nom("Circle 2").build();
        CircleDTO circle3 = CircleDTO.builder().id(3L).createurId(userId).nom("Circle 3").build();
        List<CircleDTO> activeCircles = List.of(circle1, circle2, circle3);

        when(circleClient.getCirclesActive()).thenReturn(activeCircles);

        List<CircleDTO> result = membershipService.findActiveCirclesCreatedByUser(userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> userId.equals(c.getCreateurId())));
    }

}
