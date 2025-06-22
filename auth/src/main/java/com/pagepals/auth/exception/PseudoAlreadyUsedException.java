package com.pagepals.auth.exception;

public class PseudoAlreadyUsedException extends RuntimeException {

    public PseudoAlreadyUsedException(String message){
        super(message);
    }
}
