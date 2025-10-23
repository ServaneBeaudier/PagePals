package com.pagepals.circle.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour le microservice circle-service.
 * 
 * Centralise la gestion des erreurs afin de renvoyer des réponses HTTP cohérentes
 * et compréhensibles pour le front-end ou les autres microservices.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs liées à un cercle introuvable.
     *
     * @param ex exception levée
     * @return 404 NOT FOUND avec le message associé
     */
    @ExceptionHandler(CircleNotFoundException.class)
    public ResponseEntity<String> handleCircleNotFound(CircleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Gère les cas où un livre n'a pas été trouvé.
     *
     * @param ex exception levée
     * @return 404 NOT FOUND avec le message d'erreur
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Gère les erreurs provenant d'appels à des API externes.
     *
     * @param ex exception levée
     * @return 503 SERVICE UNAVAILABLE avec le message d'erreur
     */
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApi(ExternalApiException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Erreur externe : " + ex.getMessage());
    }

    /**
     * Gère les erreurs de données invalides lors de la création ou modification d'un cercle.
     *
     * @param ex exception levée
     * @return 400 BAD REQUEST avec le message explicatif
     */
    @ExceptionHandler(InvalidCircleDataException.class)
    public ResponseEntity<String> handleInvalidData(InvalidCircleDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Gère les actions interdites à l'utilisateur (non autorisé ou non créateur).
     *
     * @param ex exception levée
     * @return 403 FORBIDDEN avec le message d'erreur
     */
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    /**
     * Gère les tentatives de modification d'un cercle déjà archivé.
     *
     * @param ex exception levée
     * @return 400 BAD REQUEST avec le message d'erreur
     */
    @ExceptionHandler(ArchivedCircleModificationException.class)
    public ResponseEntity<String> handleArchived(ArchivedCircleModificationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Gère les conflits lors de la création d'un cercle déjà existant.
     *
     * @param ex exception levée
     * @return 409 CONFLICT avec le message explicatif
     */
    @ExceptionHandler(CircleAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExists(CircleAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Gère les cas d'accès interdit à une ressource ou action spécifique.
     *
     * @param ex exception levée
     * @return 403 FORBIDDEN avec une réponse JSON simple
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Gère toute autre exception non spécifiquement interceptée.
     *
     * @param ex exception levée
     * @return 500 INTERNAL SERVER ERROR avec le message d'erreur
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur serveur : " + ex.getMessage());
    }
}
