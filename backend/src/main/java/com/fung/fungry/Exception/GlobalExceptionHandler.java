package com.fung.fungry.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FungryBaseException.class)
    public ResponseEntity<ErrorResponse> FungryBase(FungryBaseException fungryBaseException, HttpServletRequest request)
    {
        ErrorResponse response=new ErrorResponse();
        response.setErrorCode(fungryBaseException.getErrorCode());
        response.setMessage(fungryBaseException.getMessage());
        response.setErrorStatus(fungryBaseException.getErrorStatus());
        response.setTimeStamp(new Date());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response,fungryBaseException.getErrorStatus());
    }
}
