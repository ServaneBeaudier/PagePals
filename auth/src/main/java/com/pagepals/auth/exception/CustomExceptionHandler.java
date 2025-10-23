package com.pagepals.auth.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour le microservice d'authentification.
 * Intercepte les erreurs courantes et renvoie des réponses HTTP adaptées
 * avec un message clair à destination du client.
 *
 * Les exceptions personnalisées (EmailAlreadyUsedException, InvalidCredentialException, etc.)
 * permettent de traduire les erreurs métier en statuts HTTP cohérents.
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * Gère les cas où l'adresse e-mail est déjà utilisée par un autre compte.
     *
     * @param ex exception levée lors d'une tentative d'inscription avec un email existant
     * @return 409 CONFLICT avec le message d'erreur associé
     */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<String> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Gère les erreurs d'identifiants invalides lors de la tentative de connexion.
     *
     * @param ex exception levée lorsque les identifiants sont incorrects
     * @return 401 UNAUTHORIZED avec le message explicatif
     */
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    /**
     * Gère les cas où un utilisateur n'est pas trouvé dans la base de données.
     *
     * @param ex exception levée lorsqu'un utilisateur n'existe pas ou a été supprimé
     * @return 404 NOT FOUND avec le message associé
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Gère les erreurs de validation sur les DTO annotés avec {@code @Valid}.
     * Retourne une map champ → message d'erreur pour simplifier l'affichage côté front.
     *
     * @param ex exception levée lorsque des contraintes de validation ne sont pas respectées
     * @return 400 BAD REQUEST contenant les erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
