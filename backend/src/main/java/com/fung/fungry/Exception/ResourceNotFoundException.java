package com.fung.fungry.Exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends FungryBaseException{

    public  ResourceNotFoundException(String message) {
        super(message,HttpStatus.NOT_FOUND , "RESOURCE_NOT_FOUND");
    }
}
