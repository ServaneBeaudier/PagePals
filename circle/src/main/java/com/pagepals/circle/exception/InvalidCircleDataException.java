package com.pagepals.circle.exception;

/**
 * Exception levée lorsque les données fournies pour la création
 * ou la mise à jour d'un cercle littéraire sont invalides.
 * 
 * Utilisée notamment pour signaler une violation de contrainte
 * ou une incohérence dans les informations envoyées par le client.
 */
public class InvalidCircleDataException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description de la donnée invalide ou du problème rencontré
     */
    public InvalidCircleDataException(String message) {
        super(message);
    }
}
