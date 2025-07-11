package com.pagepals.circle.controllertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagepals.circle.controller.CircleController;
import com.pagepals.circle.dto.CircleDTO;
import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.MessageDTO;
import com.pagepals.circle.dto.SearchCriteriaDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;
import com.pagepals.circle.jwt.JWTUtil;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.repository.CircleRepository;
import com.pagepals.circle.service.CircleService;
import com.pagepals.circle.service.MessageService;

@ExtendWith(MockitoExtension.class)
public class CircleControllerTest {

    @Mock
    private CircleService circleService;

    @Mock
    private MessageService messageService;

    @Mock
    private CircleRepository circleRepository;

    @InjectMocks
    private CircleController circleController;

    private MockMvc mockMvc;

    @Mock
    private JWTUtil jwtUtil;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(circleController).build();
    }

    @Test
    void getCircleById_returnsOkAndCircleDTO() throws Exception {
        Long circleId = 1L;
        CircleDTO circleDTO = new CircleDTO();
        circleDTO.setId(circleId);
        circleDTO.setNom("Mon Cercle Test");

        when(circleService.getCircleById(circleId)).thenReturn(circleDTO);

        mockMvc.perform(get("/api/circles/{id}", circleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(circleId))
                .andExpect(jsonPath("$.nom").value("Mon Cercle Test"));

        verify(circleService, times(1)).getCircleById(circleId);
    }

    @Test
    void getCirclesActive_returnsListAndStatusOk() throws Exception {
        List<CircleDTO> circleList = List.of(
                CircleDTO.builder().id(1L).nom("Cercle 1").build(),
                CircleDTO.builder().id(2L).nom("Cercle 2").build());

        when(circleService.getCirclesActive()).thenReturn(circleList);

        mockMvc.perform(get("/api/circles/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(circleList.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nom").value("Cercle 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nom").value("Cercle 2"));

        verify(circleService, times(1)).getCirclesActive();
    }

    @Test
    void getCirclesArchived_returnsListAndStatusOk() throws Exception {
        List<CircleDTO> archivedList = List.of(
                CircleDTO.builder().id(3L).nom("Cercle Archivé 1").build(),
                CircleDTO.builder().id(4L).nom("Cercle Archivé 2").build());

        when(circleService.getCirclesArchived()).thenReturn(archivedList);

        mockMvc.perform(get("/api/circles/archived"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(archivedList.size()))
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].nom").value("Cercle Archivé 1"))
                .andExpect(jsonPath("$[1].id").value(4L))
                .andExpect(jsonPath("$[1].nom").value("Cercle Archivé 2"));

        verify(circleService, times(1)).getCirclesArchived();
    }

    @Test
    void createCircle_validRequest_returnsCreated() throws Exception {
        CreateCircleDTO createDTO = new CreateCircleDTO();
        createDTO.setNom("Nouveau Cercle");

        String token = "token-test";
        Long userId = 42L;

        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        doNothing().when(circleService).createCircle(any(CreateCircleDTO.class), eq(userId));

        mockMvc.perform(post("/api/circles/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createDTO)))
                .andExpect(status().isCreated());

        verify(jwtUtil, times(1)).extractUserId(token);
        verify(circleService, times(1)).createCircle(any(CreateCircleDTO.class), eq(userId));
    }

    // Méthode utilitaire pour convertir objet en JSON
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateCircle_validRequest_returnsNoContent() throws Exception {
        long circleId = 10L;
        long userId = 42L;

        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setNom("Cercle Modifié");

        doNothing().when(circleService).updateCircle(any(UpdateCircleDTO.class), eq(userId));

        mockMvc.perform(put("/api/circles/{id}", circleId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UpdateCircleDTO> dtoCaptor = ArgumentCaptor.forClass(UpdateCircleDTO.class);

        verify(circleService).updateCircle(dtoCaptor.capture(), eq(userId));

        assertEquals(circleId, dtoCaptor.getValue().getId());
        assertEquals("Cercle Modifié", dtoCaptor.getValue().getNom());
    }

    @Test
    void getMessages_returnsMessageListAndStatusOk() throws Exception {
        Long circleId = 1L;

        MessageDTO msg1 = MessageDTO.builder()
                .id(100L)
                .circleId(circleId)
                .auteurId(200L)
                .contenu("Message 1")
                .build();

        MessageDTO msg2 = MessageDTO.builder()
                .id(101L)
                .circleId(circleId)
                .auteurId(201L)
                .contenu("Message 2")
                .build();

        when(messageService.getMessagesByCircleId(circleId)).thenReturn(List.of(msg1, msg2));

        mockMvc.perform(get("/api/circles/{circleId}/messages", circleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[0].contenu").value("Message 1"))
                .andExpect(jsonPath("$[1].id").value(101L))
                .andExpect(jsonPath("$[1].contenu").value("Message 2"));

        verify(messageService, times(1)).getMessagesByCircleId(circleId);
    }

    @Test
    void envoyerMessage_validRequest_returnsOk() throws Exception {
        Long circleId = 1L;
        String token = "token-test";
        Long auteurId = 42L;

        MessageDTO dto = new MessageDTO();
        dto.setContenu("Hello message");

        when(jwtUtil.extractUserId(token)).thenReturn(auteurId);
        doNothing().when(messageService).sendMessage(any(MessageDTO.class));

        mockMvc.perform(post("/api/circles/{circleId}/messages", circleId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isOk());

        ArgumentCaptor<MessageDTO> captor = ArgumentCaptor.forClass(MessageDTO.class);
        verify(messageService).sendMessage(captor.capture());
        MessageDTO captured = captor.getValue();
        assertEquals(circleId, captured.getCircleId());
        assertEquals(auteurId, captured.getAuteurId());
        assertEquals("Hello message", captured.getContenu());
    }

    @Test
    void searchCircles_returnsListAndStatusOk() throws Exception {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        CircleDTO circleDTO = CircleDTO.builder().id(1L).nom("Cercle A").build();
        when(circleService.searchCircles(any(SearchCriteriaDTO.class))).thenReturn(List.of(circleDTO));

        mockMvc.perform(post("/api/circles/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nom").value("Cercle A"));

        verify(circleService).searchCircles(any(SearchCriteriaDTO.class));
    }

    @Test
    void getMaxMembres_returnsMaxMembers() throws Exception {
        Long circleId = 1L;
        Circle circle = new Circle();
        circle.setNbMaxMembres(15);

        when(circleRepository.findById(circleId)).thenReturn(Optional.of(circle));

        mockMvc.perform(get("/api/circles/{id}/max-membres", circleId))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));

        verify(circleRepository).findById(circleId);
    }

    @Test
    void getCirclesByCreateur_returnsList() throws Exception {
        Long userId = 5L;
        CircleDTO circleDTO = CircleDTO.builder().id(1L).nom("Cercle Créé").build();

        when(circleService.findCirclesByCreateur(userId)).thenReturn(List.of(circleDTO));

        mockMvc.perform(get("/api/circles/created-by/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Cercle Créé"));

        verify(circleService).findCirclesByCreateur(userId);
    }

    @Test
    void deleteActiveCirclesByCreateur_returnsNoContent() throws Exception {
        Long userId = 7L;

        doNothing().when(circleService).deleteActiveCirclesByCreateur(userId);

        mockMvc.perform(delete("/api/circles/active-by-createur/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(circleService).deleteActiveCirclesByCreateur(userId);
    }

    @Test
    void anonymizeUserInArchivedCircles_returnsNoContent() throws Exception {
        Long userId = 9L;

        doNothing().when(circleService).anonymizeUserInArchivedCircles(userId);

        mockMvc.perform(put("/api/circles/anonymize-user/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(circleService).anonymizeUserInArchivedCircles(userId);
    }

    @Test
    void deleteCircle_returnsNoContent() throws Exception {
        Long circleId = 11L;

        doNothing().when(circleService).deleteCircleById(circleId);

        mockMvc.perform(delete("/api/circles/{id}", circleId))
                .andExpect(status().isNoContent());

        verify(circleService).deleteCircleById(circleId);
    }

}
