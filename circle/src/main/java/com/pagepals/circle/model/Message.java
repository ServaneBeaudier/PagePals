package com.pagepals.circle.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un message échangé au sein d'un cercle littéraire.
 * 
 * Chaque message est associé à un auteur et à un cercle, et contient
 * le texte du message ainsi que la date d'envoi.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long auteurId;

    @Column(nullable = false, length = 1000)
    private String contenu;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id", nullable = false)
    private Circle circle;
}
