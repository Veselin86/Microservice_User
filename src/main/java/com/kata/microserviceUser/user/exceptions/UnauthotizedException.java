package com.kata.microserviceUser.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthotizedException extends RuntimeException {
    public UnauthotizedException(String message) {
        super(message);
    }
}
