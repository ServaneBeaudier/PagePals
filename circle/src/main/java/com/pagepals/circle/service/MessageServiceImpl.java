package com.pagepals.circle.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pagepals.circle.client.UserClient;
import com.pagepals.circle.dto.MessageDTO;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.model.Message;
import com.pagepals.circle.repository.CircleRepository;
import com.pagepals.circle.repository.MessageRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final CircleRepository circleRepository;
    private final UserClient userClient;

    @Override
    @Transactional
    public void sendMessage(MessageDTO dto) {
        Circle circle = circleRepository.findById(dto.getCircleId())
                .orElseThrow(() -> new EntityNotFoundException("Cercle introuvable"));

        Message message = Message.builder()
                .circle(circle)
                .auteurId(dto.getAuteurId())
                .contenu(dto.getContenu())
                .dateEnvoi(LocalDateTime.now())
                .build();

        messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByCircleId(long circleId) {
        List<Message> messages = messageRepository.findByCircleIdOrderByDateEnvoiAsc(circleId);

        List<Long> userIds = messages.stream()
                .map(Message::getAuteurId)
                .distinct()
                .toList();

        var userInfos = userClient.getInfosPourMessage(List.of(2L));

        return messages.stream().map(msg -> {
            var info = userInfos.stream()
                    .filter(p -> p.getAuteurId() == msg.getAuteurId())
                    .findFirst()
                    .orElse(null);

            return MessageDTO.builder()
                    .id(msg.getId())
                    .circleId(msg.getCircle().getId())
                    .auteurId(msg.getAuteurId())
                    .contenu(msg.getContenu())
                    .dateEnvoi(msg.getDateEnvoi())
                    .pseudoAuteur(info != null ? info.getPseudoAuteur() : "inconnu")
                    .photoAuteur(info != null ? info.getPhotoAuteur() : null)
                    .build();
        }).collect(Collectors.toList());
    }

}
