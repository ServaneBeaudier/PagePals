package com.pagepals.membership.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyMemberException.class)
    public ResponseEntity<String> handlerAlreadyMemberException(AlreadyMemberException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(TooLateToRegisterException.class)
    public ResponseEntity<String> handlerTooLateToRegisterException(TooLateToRegisterException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MembershipNotFoundException.class)
    public ResponseEntity<String> handlerMembershipNotFoundException(MembershipNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
