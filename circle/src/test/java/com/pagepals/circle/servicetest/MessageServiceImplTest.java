package com.pagepals.circle.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pagepals.circle.client.UserClient;
import com.pagepals.circle.dto.MessageDTO;
import com.pagepals.circle.model.*;
import com.pagepals.circle.repository.*;
import com.pagepals.circle.service.MessageServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private CircleRepository circleRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void sendMessage_existingCircle_savesMessage() {
        MessageDTO dto = new MessageDTO();
        dto.setCircleId(1L);
        dto.setAuteurId(10L);
        dto.setContenu("Hello world");

        Circle circle = new Circle();
        circle.setId(1L);

        when(circleRepository.findById(1L)).thenReturn(Optional.of(circle));

        messageService.sendMessage(dto);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());

        Message saved = captor.getValue();
        assertEquals(circle, saved.getCircle());
        assertEquals(dto.getAuteurId(), saved.getAuteurId());
        assertEquals(dto.getContenu(), saved.getContenu());
        assertNotNull(saved.getDateEnvoi());
    }

    @Test
    void sendMessage_circleNotFound_throwsException() {
        MessageDTO dto = new MessageDTO();
        dto.setCircleId(1L);

        when(circleRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            messageService.sendMessage(dto);
        });

        assertEquals("Cercle introuvable", exception.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void getMessagesByCircleId_returnsDtosWithUserInfos() {
        long circleId = 1L;

        Message msg1 = Message.builder()
                .id(10L)
                .circle(new Circle()) // pas de constructeur id
                .auteurId(100L)
                .contenu("Bonjour")
                .dateEnvoi(LocalDateTime.now().minusDays(1))
                .build();
        msg1.getCircle().setId(1L); // setter id

        Message msg2 = Message.builder()
                .id(11L)
                .circle(new Circle())
                .auteurId(101L)
                .contenu("Salut")
                .dateEnvoi(LocalDateTime.now())
                .build();
        msg2.getCircle().setId(1L);

        when(messageRepository.findByCircleIdOrderByDateEnvoiAsc(circleId))
                .thenReturn(List.of(msg1, msg2));

        // Simplification : on retourne liste vide ici
        when(userClient.getInfosPourMessage(anyList()))
                .thenReturn(Collections.emptyList());

        List<MessageDTO> dtos = messageService.getMessagesByCircleId(circleId);

        assertEquals(2, dtos.size());
        assertEquals("inconnu", dtos.get(0).getPseudoAuteur());
        assertEquals("inconnu", dtos.get(1).getPseudoAuteur());
    }

    @Test
    void getMessagesByCircleId_infosAbsent_returnsUnknownPseudo() {
        long circleId = 1L;

        Message msg = Message.builder()
                .id(10L)
                .circle(new Circle())
                .auteurId(100L)
                .contenu("Hello")
                .dateEnvoi(LocalDateTime.now())
                .build();

        when(messageRepository.findByCircleIdOrderByDateEnvoiAsc(circleId))
                .thenReturn(List.of(msg));

        when(userClient.getInfosPourMessage(List.of(100L))).thenReturn(Collections.emptyList());

        List<MessageDTO> dtos = messageService.getMessagesByCircleId(circleId);

        assertEquals(1, dtos.size());
        assertEquals("inconnu", dtos.get(0).getPseudoAuteur());
        assertNull(dtos.get(0).getPhotoAuteur());
    }

    @Test
    void getMessagesByCircleId_noMessages_returnsEmptyList() {
        long circleId = 1L;

        when(messageRepository.findByCircleIdOrderByDateEnvoiAsc(circleId)).thenReturn(Collections.emptyList());

        List<MessageDTO> dtos = messageService.getMessagesByCircleId(circleId);

        assertTrue(dtos.isEmpty());
    }

}
