package com.example.exception;

public class AuthenticationFailException extends RuntimeException{
    public AuthenticationFailException(String message) {
        super(message);
    }
}
