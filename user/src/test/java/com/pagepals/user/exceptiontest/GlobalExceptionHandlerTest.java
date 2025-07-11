package com.pagepals.user.exceptiontest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pagepals.user.exception.GlobalExceptionHandler;
import com.pagepals.user.exception.UserNotFoundException;

public class GlobalExceptionHandlerTest {

    @Test
    void handleEntityNotFound_shouldReturnNotFoundResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserNotFoundException ex = new UserNotFoundException("Utilisateur non trouvé");

        ResponseEntity<String> response = handler.handleEntityNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utilisateur non trouvé", response.getBody());
    }
}
