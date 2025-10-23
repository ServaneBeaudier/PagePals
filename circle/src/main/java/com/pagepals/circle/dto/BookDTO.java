package com.pagepals.circle.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente un livre retourné lors d'une recherche ou associé à un cercle littéraire.
 * 
 * Contient les informations principales du livre telles que le titre, les auteurs,
 * l'ISBN, le genre et l'URL de la couverture.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private String titre;

    private List<String> auteurs;

    private String isbn;

    private String genre;

    private String couvertureUrl;
}
