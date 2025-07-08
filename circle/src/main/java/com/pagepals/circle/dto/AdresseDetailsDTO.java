package com.pagepals.circle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
