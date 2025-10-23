package com.pagepals.circle.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagepals.circle.client.UserClient;
import com.pagepals.circle.dto.MessageDTO;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.model.Message;
import com.pagepals.circle.repository.CircleRepository;
import com.pagepals.circle.repository.MessageRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Implémentation du service de gestion des messages d’un cercle littéraire.
 * 
 * Permet l’envoi et la récupération des messages échangés au sein d’un cercle,
 * en enrichissant les données avec les informations des auteurs via user-service.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final CircleRepository circleRepository;
    private final UserClient userClient;

    /**
     * Enregistre un nouveau message dans un cercle donné.
     * 
     * @param dto données du message à enregistrer
     * @throws EntityNotFoundException si le cercle spécifié n’existe pas
     */
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

    /**
     * Récupère tous les messages d’un cercle, triés par date d’envoi,
     * et enrichis avec les informations des auteurs depuis user-service.
     *
     * @param circleId identifiant du cercle
     * @return liste des messages associés au cercle, avec les métadonnées des auteurs
     */
    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByCircleId(long circleId) {
        List<Message> messages = messageRepository.findByCircleIdOrderByDateEnvoiAsc(circleId);

        // Récupération des IDs uniques des auteurs
        List<Long> userIds = messages.stream()
                .map(Message::getAuteurId)
                .distinct()
                .toList();

        var userInfos = userClient.getInfosPourMessage(userIds);

        // Mapping entité → DTO avec enrichissement des infos auteur
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
