package com.kata.microserviceUser.user.userDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordRequest {

    @NotBlank(message = "Password is obligatory")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String existingPassword;

    @NotBlank(message = "Password is obligatory")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String newPassword;

    public UserPasswordRequest() {
    }

    public UserPasswordRequest(String existingPassword, String newPassword) {
        this.existingPassword = existingPassword;
        this.newPassword = newPassword;
    }

    public String getExistingPassword() {
        return existingPassword;
    }

    public void setExistingPassword(String existingPassword) {
        this.existingPassword = existingPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
