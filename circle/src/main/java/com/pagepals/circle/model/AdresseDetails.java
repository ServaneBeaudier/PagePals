package com.pagepals.circle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les détails d'une adresse associés à un cercle littéraire.
 * 
 * Utilisée côté modèle pour stocker ou manipuler les informations
 * de localisation d'un cercle en présentiel.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdresseDetails {

    private String shop;
    private String houseNumber;
    private String road;
    private String postcode;
    private String city;
}
