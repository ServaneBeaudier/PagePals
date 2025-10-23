package com.pagepals.circle.dto;

import lombok.Data;

/**
 * Requête utilisée pour rechercher des livres à partir d'un critère de recherche.
 * 
 * Le critère peut correspondre à un titre, un auteur ou un mot-clé.
 */
@Data
public class SearchBookDTO {

   private String critereRecherche;
}
