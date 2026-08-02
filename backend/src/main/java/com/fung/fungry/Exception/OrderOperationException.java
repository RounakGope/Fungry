package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class OrderOperationException extends FungryBaseException{
    public OrderOperationException(String message) {
        super(message, HttpStatus.CONFLICT, "OrderOperationException");
    }
}
