package com.pagepals.circle.exceptiontest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.circle.exception.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TestController testController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class TestController {

        @GetMapping("/circle-not-found")
        public void circleNotFound() {
            throw new CircleNotFoundException("Cercle introuvable");
        }

        @GetMapping("/book-not-found")
        public void bookNotFound() {
            throw new BookNotFoundException("Livre introuvable");
        }

        @GetMapping("/external-api")
        public void externalApi() {
            throw new ExternalApiException("Erreur API externe");
        }

        @GetMapping("/invalid-data")
        public void invalidData() {
            throw new InvalidCircleDataException("Données invalides");
        }

        @GetMapping("/unauthorized")
        public void unauthorized() {
            throw new UnauthorizedActionException("Action non autorisée");
        }

        @GetMapping("/archived")
        public void archived() {
            throw new ArchivedCircleModificationException("Cercle archivé");
        }

        @GetMapping("/already-exists")
        public void alreadyExists() {
            throw new CircleAlreadyExistsException("Cercle déjà existant");
        }

        @GetMapping("/access-denied")
        public void accessDenied() {
            throw new AccessDeniedException("Accès refusé");
        }

        @GetMapping("/fallback")
        public void fallback() {
            throw new RuntimeException("Erreur inconnue");
        }
    }

    @Test
    void handleCircleNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/circle-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cercle introuvable"));
    }

    @Test
    void handleBookNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/book-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Livre introuvable"));
    }

    @Test
    void handleExternalApi_returnsServiceUnavailable() throws Exception {
        mockMvc.perform(get("/external-api"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Erreur externe : Erreur API externe"));
    }

    @Test
    void handleInvalidData_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/invalid-data"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Données invalides"));
    }

    @Test
    void handleUnauthorized_returnsForbidden() throws Exception {
        mockMvc.perform(get("/unauthorized"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Action non autorisée"));
    }

    @Test
    void handleArchived_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/archived"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cercle archivé"));
    }

    @Test
    void handleAlreadyExists_returnsConflict() throws Exception {
        mockMvc.perform(get("/already-exists"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cercle déjà existant"));
    }

    @Test
    void handleAccessDenied_returnsForbiddenWithJson() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Accès refusé"));
    }

    @Test
    void handleFallback_returnsInternalServerError() throws Exception {
        mockMvc.perform(get("/fallback"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erreur serveur : Erreur inconnue"));
    }
}
