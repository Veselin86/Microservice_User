package com.kata.microserviceUser.user.config;

import com.kata.microserviceUser.user.userDTO.UserRequestCreate;

import java.util.ArrayList;
import java.util.List;

public class DataValidator {

    public List<String> validate(UserRequestCreate userRequestCreate) {
        List<String> errors = new ArrayList<>();

        if (userRequestCreate.getUsername() == null) {
            errors.add("Username is obligatory");
        }
        if (userRequestCreate.getName() == null) {
            errors.add("Name can't be blank");
        }
        if (userRequestCreate.getEmail() == null) {
            errors.add("Email is obligatory");
        }
        if (userRequestCreate.getPassword() == null) {
            errors.add("Password is obligatory");
        }
        if (userRequestCreate.getPassword().length() < 4) {
            errors.add("Password should be at least 4 characters");
        }

        return errors;
    }

}
