package com.kata.microserviceUser.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DataIntegrityViolationException extends Exception{
    public DataIntegrityViolationException(String message) {
        super(message);
    }
}
