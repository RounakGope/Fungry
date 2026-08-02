package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class RestaurantOperationException extends FungryBaseException{
    public RestaurantOperationException(String message) {
        super(message, HttpStatus.CONFLICT, "RestaurantOperationException");
    }
}
