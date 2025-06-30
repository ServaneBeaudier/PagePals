package com.pagepals.membership.exception;

public class AlreadyMemberException extends RuntimeException{

    public AlreadyMemberException(String message) {
        super(message);
    }
}
