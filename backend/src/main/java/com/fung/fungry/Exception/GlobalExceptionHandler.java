package com.fung.fungry.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    // Thrown when a @PreAuthorize check fails (e.g. a non-admin calling /admin/**)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException exception, HttpServletRequest request)
    {
        ErrorResponse response=new ErrorResponse();
        response.setErrorCode("FORBIDDEN");
        response.setMessage("You don't have permission to do this");
        response.setErrorStatus(HttpStatus.FORBIDDEN);
        response.setTimeStamp(new Date());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
    }
}
