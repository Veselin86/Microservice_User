package com.kata.microserviceUser.user.exceptions;

import org.springframework.validation.BindingResult;

public class UniqueFieldException extends RuntimeException {
    public UniqueFieldException(String message) {
        super(message);
    }

    public BindingResult getBindingResult() {
        return null;
    }
}