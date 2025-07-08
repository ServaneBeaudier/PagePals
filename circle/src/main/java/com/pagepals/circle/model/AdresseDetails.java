package com.pagepals.circle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
