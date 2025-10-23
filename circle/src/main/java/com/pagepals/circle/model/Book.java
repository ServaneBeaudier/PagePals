package com.pagepals.circle.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un livre associé à un cercle littéraire.
 * 
 * Un livre peut être proposé lors de la création d'un cercle pour servir de support de discussion.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "livre")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titre;

    @ElementCollection
    private List<String> auteurs;

    private String isbn;

    private String genre;

    private String couvertureUrl;

}
