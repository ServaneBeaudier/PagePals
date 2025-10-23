package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'un livre recherché ou référencé
 * n'a pas été trouvé dans les résultats ou la base de données.
 * 
 * Utilisée notamment lors de la recherche d'un ouvrage pour un cercle littéraire.
 */
public class BookNotFoundException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du problème rencontré
     */
    public BookNotFoundException(String message) {
        super(message);
    }
}
