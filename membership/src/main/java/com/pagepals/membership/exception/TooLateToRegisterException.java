package com.pagepals.membership.exception;

public class TooLateToRegisterException extends RuntimeException{

    public TooLateToRegisterException(String message) {
        super(message);
    }

}
