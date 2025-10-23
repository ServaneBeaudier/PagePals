package com.pagepals.circle.exception;

/**
 * Exception levée lorsqu'un utilisateur tente d'effectuer une action
 * pour laquelle il ne dispose pas des autorisations nécessaires.
 * 
 * Utilisée notamment lors de la modification ou suppression d'un cercle
 * par un utilisateur non créateur ou non autorisé.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message d'erreur fourni.
     *
     * @param message description du problème d'accès rencontré
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}
