package com.pagepals.auth.exception;

public class EmailAlreadyUsedException extends RuntimeException{

    public EmailAlreadyUsedException(String message){
        super(message);
    }

}
