package com.kata.microserviceUser.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MethodArgumentNotValidException extends Exception {
    public MethodArgumentNotValidException(String message) {
        super(message);
    }
}
