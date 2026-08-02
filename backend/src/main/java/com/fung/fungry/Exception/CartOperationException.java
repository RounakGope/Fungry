package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class CartOperationException extends FungryBaseException{
    public CartOperationException(String message) {
        super(message, HttpStatus.CONFLICT, "CartExceptionError");
    }
}
