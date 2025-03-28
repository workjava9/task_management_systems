package com.example.exception;

public class ValidationFailException extends RuntimeException {
    public ValidationFailException(String message) {
        super(message);
    }

}
