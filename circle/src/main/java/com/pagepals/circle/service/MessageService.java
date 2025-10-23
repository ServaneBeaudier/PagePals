package com.pagepals.circle.service;

import java.util.List;

import com.pagepals.circle.dto.MessageDTO;

/**
 * Interface définissant la logique métier de gestion des messages
 * échangés au sein des cercles littéraires.
 */
public interface MessageService {

    /**
     * Envoie un message dans un cercle.
     *
     * @param dto données du message à envoyer
     */
    void sendMessage(MessageDTO dto);

    /**
     * Récupère la liste des messages d'un cercle, triés chronologiquement.
     *
     * @param circleId identifiant du cercle
     * @return liste des messages du cercle
     */
    List<MessageDTO> getMessagesByCircleId(long circleId);
}
