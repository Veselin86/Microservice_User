package com.kata.microserviceUser.user.userDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordRequest {

    @NotBlank(message = "Password is obligatory")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
