package com.pagepals.circle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CircleNotFoundException.class)
    public ResponseEntity<String> handleCircleNotFound(CircleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApi(ExternalApiException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Erreur externe : " + ex.getMessage());
    }

    @ExceptionHandler(InvalidCircleDataException.class)
    public ResponseEntity<String> handleInvalidData(InvalidCircleDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(ArchivedCircleModificationException.class)
    public ResponseEntity<String> handleArchived(ArchivedCircleModificationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CircleAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExists(CircleAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage()); // 409
    }

    // Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur : " + ex.getMessage());
    }
}
