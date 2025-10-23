package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'une tentative de création d'un cercle
 * entre en conflit avec un cercle déjà existant.
 * 
 * Utilisée pour éviter la duplication de cercles similaires
 * selon des critères comme le nom, la date ou le créateur.
 */
public class CircleAlreadyExistsException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du conflit détecté
     */
    public CircleAlreadyExistsException(String message) {
        super(message);
    }
}
