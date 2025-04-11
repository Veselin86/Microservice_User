package com.kata.microserviceUser.user.config;

import com.kata.microserviceUser.user.userDTO.UserRequest;

import java.util.ArrayList;
import java.util.List;

public class DataValidator {

    public List<String> validate(UserRequest userRequest) {
        List<String> errors = new ArrayList<>();

        if (userRequest.getUsername() == null) {
            errors.add("Username is obligatory");
        }
        if (userRequest.getName() == null) {
            errors.add("Name can't be blank");
        }
        if (userRequest.getEmail() == null) {
            errors.add("Email is obligatory");
        }
        if (userRequest.getPassword() == null) {
            errors.add("Password is obligatory");
        }
        if (userRequest.getPassword().length() < 4) {
            errors.add("Password should be at least 4 characters");
        }

        return errors;
    }

}
