package com.pagepals.membership.controllertest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagepals.membership.controller.MembershipController;
import com.pagepals.membership.dto.*;
import com.pagepals.membership.service.MembershipService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MembershipController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembershipService membershipService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void inscrire_shouldReturnOk() throws Exception {
        MembershipRequestDTO dto = new MembershipRequestDTO();
        dto.setUserId(1L);
        dto.setCircleId(2L);

        mockMvc.perform(post("/api/memberships/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(membershipService).inscrire(1L, 2L);
    }

    @Test
    void desinscrire_shouldReturnOk() throws Exception {
        MembershipRequestDTO dto = new MembershipRequestDTO();
        dto.setUserId(1L);
        dto.setCircleId(2L);

        mockMvc.perform(delete("/api/memberships/desinscrire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(membershipService).desinscrire(1L, 2L);
    }

    @Test
    void estInscrit_shouldReturnTrue() throws Exception {
        when(membershipService.estInscrit(1L, 2L)).thenReturn(true);

        mockMvc.perform(get("/api/memberships/check")
                .param("userId", "1")
                .param("circleId", "2"))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        verify(membershipService).estInscrit(1L, 2L);
    }

    @Test
    void getParticipantsWithPseudo_shouldReturnList() throws Exception {
        ParticipantDTO participant = new ParticipantDTO();
        participant.setId(1L);
        participant.setPseudo("pseudo");
        participant.setPhotoProfil("photo.jpg");

        when(membershipService.getParticipantsWithPseudo(2L)).thenReturn(List.of(participant));

        mockMvc.perform(get("/api/memberships/circle/2/participants"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].pseudo", is("pseudo")))
            .andExpect(jsonPath("$[0].photoProfil", is("photo.jpg")));

        verify(membershipService).getParticipantsWithPseudo(2L);
    }

    @Test
    void getMemberCount_shouldReturnInt() throws Exception {
        when(membershipService.countMembersForCircle(2L)).thenReturn(5);

        mockMvc.perform(get("/api/memberships/count/2"))
            .andExpect(status().isOk())
            .andExpect(content().string("5"));

        verify(membershipService).countMembersForCircle(2L);
    }

    @Test
    void ajouterCreateurCommeMembre_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/memberships/auto-inscription")
                .param("circleId", "2")
                .param("userId", "1"))
            .andExpect(status().isOk());

        verify(membershipService).inscrire(2L, 1L);
    }

    @Test
    void supprimerInscriptions_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/memberships/by-user/1"))
            .andExpect(status().isNoContent());

        verify(membershipService).supprimerToutesLesInscriptionsPourUtilisateur(1L);
    }

    @Test
    void getCirclesByUser_shouldReturnList() throws Exception {
        CircleDTO circle = CircleDTO.builder().id(1L).nom("Circle 1").build();

        when(membershipService.findActiveCirclesJoinedByUser(1L)).thenReturn(List.of(circle));

        mockMvc.perform(get("/api/memberships/by-user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nom", is("Circle 1")));

        verify(membershipService).findActiveCirclesJoinedByUser(1L);
    }

    @Test
    void getCirclesByCreator_shouldReturnList() throws Exception {
        CircleDTO circle = CircleDTO.builder().id(2L).nom("Circle 2").build();

        when(membershipService.findActiveCirclesCreatedByUser(2L)).thenReturn(List.of(circle));

        mockMvc.perform(get("/api/memberships/created-by/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(2)))
            .andExpect(jsonPath("$[0].nom", is("Circle 2")));

        verify(membershipService).findActiveCirclesCreatedByUser(2L);
    }
}
