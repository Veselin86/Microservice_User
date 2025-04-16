package com.kata.microserviceUser.user.userDTO;

import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Validated
public class UserRequestUpdate {

    @NotBlank(message = "Username is obligatory")
    private String username;

    @NotBlank(message = "Name can't be blank")
    private String name;

    @NotBlank(message = "Email is obligatory")
    @Email(message = "Not valid email")
    private String email;

    public UserRequestUpdate() {
    }

    public UserRequestUpdate(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRequestUpdate that = (UserRequestUpdate) o;
        return Objects.equals(username, that.username) && Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, email);
    }
}
