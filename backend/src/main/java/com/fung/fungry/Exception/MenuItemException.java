package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class MenuItemException extends FungryBaseException{
    public MenuItemException(String message) {
        super(message, HttpStatus.CONFLICT, "MenuItemException");
    }
}
