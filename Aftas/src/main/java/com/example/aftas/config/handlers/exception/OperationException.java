package com.example.aftas.config.handlers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OperationException extends RuntimeException{
    public OperationException(String message) {
        super(message);
    }
}
