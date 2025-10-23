package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'une erreur survient lors d'un appel
 * à une API externe (exemple : recherche de livres via un service tiers).
 * 
 * Permet d'encapsuler les erreurs réseau, les réponses invalides
 * ou les exceptions remontées par les bibliothèques d'appel HTTP.
 */
public class ExternalApiException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec un message descriptif.
     *
     * @param message description de l'erreur rencontrée
     */
    public ExternalApiException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec un message et la cause initiale.
     *
     * @param message description de l'erreur rencontrée
     * @param cause exception d'origine ayant provoqué cette erreur
     */
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
