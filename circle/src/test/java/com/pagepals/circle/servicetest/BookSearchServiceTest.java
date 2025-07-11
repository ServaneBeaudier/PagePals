package com.pagepals.circle.servicetest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pagepals.circle.service.BookSearchService;

public class BookSearchServiceTest {

    @Test
    void constructor_throwsException_whenApiKeyMissing() {
        // Ici on simule le dossier sans .env avec clé
        System.setProperty("user.dir", "chemin/vers/dossier_sans_env");
        Exception e = assertThrows(IllegalStateException.class, () -> {
            new BookSearchService();
        });
        assertTrue(e.getMessage().contains("Clé API Google Books non trouvée"));
    }

}
