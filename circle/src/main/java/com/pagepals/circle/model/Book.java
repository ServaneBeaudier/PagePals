package com.pagepals.circle.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;
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
