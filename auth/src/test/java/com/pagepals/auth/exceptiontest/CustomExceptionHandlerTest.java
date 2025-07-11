package com.pagepals.auth.exceptiontest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.pagepals.auth.exception.*;

class CustomExceptionHandlerTest {

    private CustomExceptionHandler handler;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        handler = new CustomExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController(handler)).build();
    }

    static class TestController {
        private final CustomExceptionHandler handler;

        TestController(CustomExceptionHandler handler) {
            this.handler = handler;
        }

    }

    @Test
    void handleEmailAlreadyUsedException_returnsConflict() {
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException("Email déjà utilisé");

        ResponseEntity<String> response = handler.handleEmailAlreadyUsed(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
        assertEquals("Email déjà utilisé", response.getBody());
    }

    @Test
    void handleInvalidCredentialException_returnsUnauthorized() {
        InvalidCredentialException ex = new InvalidCredentialException("Identifiants invalides");

        ResponseEntity<String> response = handler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Identifiants invalides", response.getBody());
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        UserNotFoundException ex = new UserNotFoundException("Utilisateur introuvable");

        ResponseEntity<String> response = handler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utilisateur introuvable", response.getBody());
    }

    @Test
    void handleValidationErrors_returnsBadRequest() {
        // Mock d'un FieldError
        FieldError fieldError = new FieldError("objectName", "field", "message d'erreur");

        BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(),
                "objectName");
        bindingResult.addError(fieldError);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<java.util.Map<String, String>> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Collections.singletonMap("field", "message d'erreur"), response.getBody());
    }
}
