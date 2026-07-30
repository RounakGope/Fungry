package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class UnauthorisedException extends FungryBaseException{

    public UnauthorisedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "UNAUTHORISED");
    }
}
