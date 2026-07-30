package com.fung.fungry.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class FungryBaseException extends RuntimeException {
    private final HttpStatus errorStatus;
    private final String errorCode;
    public FungryBaseException(String message,HttpStatus status,String errorCode) {
        super(message);
        this.errorCode=errorCode;
        this.errorStatus=status;
    }
}
