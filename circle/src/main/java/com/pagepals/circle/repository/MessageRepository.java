package com.pagepals.circle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Message;

/**
 * Repository JPA pour la gestion des entités {@link Message}.
 * 
 * Fournit les opérations CRUD de base et des méthodes personnalisées
 * pour récupérer les messages d'un cercle littéraire.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Récupère la liste des messages associés à un cercle donné,
     * triés par date d'envoi croissante.
     *
     * @param circleId identifiant du cercle
     * @return liste des messages classés chronologiquement
     */
    List<Message> findByCircleIdOrderByDateEnvoiAsc(Long circleId);
}
