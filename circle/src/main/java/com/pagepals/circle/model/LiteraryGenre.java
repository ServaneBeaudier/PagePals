package com.pagepals.circle.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "genre_litteraire")
public class LiteraryGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name ="nom_genre", nullable = false, unique = true)
    private String nomGenre;

}
