package com.pagepals.circle.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les critères utilisés pour rechercher des cercles littéraires.
 * 
 * Permet de filtrer les cercles selon différents paramètres tels que le genre,
 * le format, la date ou un mot-clé spécifique.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteriaDTO {

    private String motCle;

    private String genre;

    private String format;

    private LocalDate date;

    private LocalDate dateExacte;
}
