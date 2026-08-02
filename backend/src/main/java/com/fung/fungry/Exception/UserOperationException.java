package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class UserOperationException extends FungryBaseException{
    public UserOperationException(String message) {
        super(message, HttpStatus.CONFLICT, "UserOperationsException");
    }
}
