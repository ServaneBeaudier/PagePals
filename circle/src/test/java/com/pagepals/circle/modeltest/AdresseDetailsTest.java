package com.pagepals.circle.modeltest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.pagepals.circle.model.AdresseDetails;

class AdresseDetailsTest {

    @Test
    void testGettersSetters() {
        AdresseDetails adresse = new AdresseDetails();

        adresse.setShop("ShopName");
        adresse.setHouseNumber("12B");
        adresse.setRoad("Rue de Paris");
        adresse.setPostcode("75001");
        adresse.setCity("Paris");

        assertEquals("ShopName", adresse.getShop());
        assertEquals("12B", adresse.getHouseNumber());
        assertEquals("Rue de Paris", adresse.getRoad());
        assertEquals("75001", adresse.getPostcode());
        assertEquals("Paris", adresse.getCity());
    }

    @Test
    void testBuilder() {
        AdresseDetails adresse = AdresseDetails.builder()
                .shop("ShopName")
                .houseNumber("12B")
                .road("Rue de Paris")
                .postcode("75001")
                .city("Paris")
                .build();

        assertEquals("ShopName", adresse.getShop());
        assertEquals("12B", adresse.getHouseNumber());
        assertEquals("Rue de Paris", adresse.getRoad());
        assertEquals("75001", adresse.getPostcode());
        assertEquals("Paris", adresse.getCity());
    }

    @Test
    void testEqualsAndHashCode() {
        AdresseDetails adresse1 = AdresseDetails.builder()
                .shop("ShopName")
                .houseNumber("12B")
                .road("Rue de Paris")
                .postcode("75001")
                .city("Paris")
                .build();

        AdresseDetails adresse2 = AdresseDetails.builder()
                .shop("ShopName")
                .houseNumber("12B")
                .road("Rue de Paris")
                .postcode("75001")
                .city("Paris")
                .build();

        assertEquals(adresse1, adresse2);
        assertEquals(adresse1.hashCode(), adresse2.hashCode());
    }

    @Test
    void testToString() {
        AdresseDetails adresse = AdresseDetails.builder()
                .shop("ShopName")
                .houseNumber("12B")
                .road("Rue de Paris")
                .postcode("75001")
                .city("Paris")
                .build();

        String str = adresse.toString();
        assertTrue(str.contains("shop=ShopName"));
        assertTrue(str.contains("houseNumber=12B"));
        assertTrue(str.contains("road=Rue de Paris"));
        assertTrue(str.contains("postcode=75001"));
        assertTrue(str.contains("city=Paris"));
    }
}
