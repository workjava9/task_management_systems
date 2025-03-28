package com.example.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {ValidationFailException.class, ObjectNotFoundException.class})
    protected ResponseEntity<Object> requestConflict(RuntimeException ex, WebRequest request) {
        String bodyOfErrorResponse = "Incorrect request. ";
        bodyOfErrorResponse += ex.getMessage();
        return handleExceptionInternal(ex, bodyOfErrorResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }


    @ExceptionHandler(value = AuthorizationFailException.class)
    protected ResponseEntity<Object> authorizationConflict(RuntimeException ex, WebRequest request) {
        String bodyOfErrorResponse = "Unauthorized request. ";
        bodyOfErrorResponse += ex.getMessage();
        return handleExceptionInternal(ex, bodyOfErrorResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = AuthenticationFailException.class)
    protected ResponseEntity<Object> authenticationConflict(RuntimeException ex, WebRequest request) {
        String bodyOfErrorResponse = "Authentication fail. ";
        bodyOfErrorResponse += ex.getMessage();
        return handleExceptionInternal(ex, bodyOfErrorResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
