package com.pagepals.circle.dtotest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.dto.AdresseDetailsDTO;

class AdresseDetailsDTOTest {

    @Test
    void testGettersSetters() {
        AdresseDetailsDTO dto = new AdresseDetailsDTO();
        dto.setShop("ShopName");
        dto.setHouseNumber("12B");
        dto.setRoad("Main Street");
        dto.setPostcode("12345");
        dto.setCity("Paris");

        assertEquals("ShopName", dto.getShop());
        assertEquals("12B", dto.getHouseNumber());
        assertEquals("Main Street", dto.getRoad());
        assertEquals("12345", dto.getPostcode());
        assertEquals("Paris", dto.getCity());
    }

    @Test
    void testBuilder() {
        AdresseDetailsDTO dto = AdresseDetailsDTO.builder()
                .shop("ShopName")
                .houseNumber("12B")
                .road("Main Street")
                .postcode("12345")
                .city("Paris")
                .build();

        assertEquals("ShopName", dto.getShop());
        assertEquals("12B", dto.getHouseNumber());
        assertEquals("Main Street", dto.getRoad());
        assertEquals("12345", dto.getPostcode());
        assertEquals("Paris", dto.getCity());
    }
}
