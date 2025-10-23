package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'un cercle littéraire est introuvable
 * dans la base de données ou ne correspond à aucun identifiant fourni.
 * 
 * Utilisée dans les opérations de consultation, de mise à jour
 * ou de suppression de cercles.
 */
public class CircleNotFoundException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du problème rencontré
     */
    public CircleNotFoundException(String message) {
        super(message);
    }
}
