package com.fung.fungry.Configuration;


import com.fung.fungry.Exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

@Component
public class RestAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse error = new ErrorResponse();
        error.setErrorCode("UNAUTHORIZED");
        error.setMessage(authException.getMessage() != null ? authException.getMessage() : "Please log in");
        error.setErrorStatus(HttpStatus.UNAUTHORIZED);
        error.setTimeStamp(new Date());
        error.setPath(request.getRequestURI());

        new ObjectMapper().writeValue(response.getWriter(), error);
    }
}