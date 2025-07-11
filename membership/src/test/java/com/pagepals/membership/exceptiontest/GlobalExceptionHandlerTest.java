package com.pagepals.membership.exceptiontest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pagepals.membership.exception.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlerAlreadyMemberException_returnsNotFound() {
        AlreadyMemberException ex = new AlreadyMemberException("Déjà membre");
        ResponseEntity<String> response = handler.handlerAlreadyMemberException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Déjà membre", response.getBody());
    }

    @Test
    void handlerTooLateToRegisterException_returnsNotFound() {
        TooLateToRegisterException ex = new TooLateToRegisterException("Trop tard");
        ResponseEntity<String> response = handler.handlerTooLateToRegisterException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Trop tard", response.getBody());
    }

    @Test
    void handlerMembershipNotFoundException_returnsNotFound() {
        MembershipNotFoundException ex = new MembershipNotFoundException("Inscription introuvable");
        ResponseEntity<String> response = handler.handlerMembershipNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Inscription introuvable", response.getBody());
    }
}
