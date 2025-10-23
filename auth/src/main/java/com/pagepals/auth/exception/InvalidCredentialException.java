package com.pagepals.auth.exception;

/**
 * Exception levée lorsque les identifiants de connexion fournis par l'utilisateur
 * (email ou mot de passe) sont invalides.
 *
 * Déclenche une réponse HTTP 401 (UNAUTHORIZED) via le gestionnaire global d'exceptions.
 */
public class InvalidCredentialException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du problème rencontré
     */
    public InvalidCredentialException(String message) {
        super(message);
    }
}
