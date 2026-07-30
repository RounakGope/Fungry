package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends FungryBaseException{
    public UnauthenticatedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED,"AUTHENTICATION_REQUIRED");
    }
}
