package com.pagepals.auth.exception;

public class InvalidCredentialException extends RuntimeException{

    public InvalidCredentialException(String message) {
        super(message);
    }

}
