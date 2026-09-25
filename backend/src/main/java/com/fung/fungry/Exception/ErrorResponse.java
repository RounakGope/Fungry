package com.fung.fungry.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String errorCode;
    private HttpStatus errorStatus;
    private Date timeStamp;
    private String path;
}
