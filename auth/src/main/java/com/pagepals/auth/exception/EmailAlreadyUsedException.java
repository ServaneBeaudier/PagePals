package com.pagepals.auth.exception;

/**
 * Exception levée lorsqu'une tentative d'inscription utilise une adresse e-mail
 * déjà enregistrée dans la base de données.
 *
 * Permet d'informer le contrôleur afin de renvoyer une réponse HTTP 409 (CONFLICT).
 */
public class EmailAlreadyUsedException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du problème rencontré
     */
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
