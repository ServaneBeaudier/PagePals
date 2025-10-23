package com.pagepals.circle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les détails d'une adresse associés à un cercle littéraire.
 * 
 * Utilisé notamment pour les cercles en présentiel afin de stocker
 * les informations de localisation précises.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdresseDetailsDTO {

    private String shop;
    private String houseNumber;
    private String road;
    private String postcode;
    private String city;
}
