package com.pagepals.auth.exception;

/**
 * Exception levée lorsqu'un utilisateur est introuvable dans la base de données.
 * 
 * Généralement utilisée lors d'opérations nécessitant un utilisateur existant
 * (ex. mise à jour de compte, suppression, récupération d'informations).
 *
 * Déclenche une réponse HTTP 404 (NOT FOUND) via le gestionnaire global d'exceptions.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du problème rencontré
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
