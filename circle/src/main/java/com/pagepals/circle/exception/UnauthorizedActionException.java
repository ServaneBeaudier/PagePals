package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'un utilisateur tente d'exécuter une action
 * qu'il n'est pas autorisé à effectuer.
 * 
 * Par exemple, lorsqu'un utilisateur essaie de modifier ou supprimer
 * un cercle dont il n'est pas le créateur.
 */
public class UnauthorizedActionException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description de la violation d'autorisation
     */
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
