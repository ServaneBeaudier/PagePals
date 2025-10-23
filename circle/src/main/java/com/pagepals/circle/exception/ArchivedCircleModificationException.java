package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'une tentative de modification est effectuée
 * sur un cercle déjà archivé.
 * 
 * Les cercles archivés ne peuvent plus être modifiés ni supprimés.
 */
public class ArchivedCircleModificationException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description de l'erreur rencontrée
     */
    public ArchivedCircleModificationException(String message) {
        super(message);
    }
}
