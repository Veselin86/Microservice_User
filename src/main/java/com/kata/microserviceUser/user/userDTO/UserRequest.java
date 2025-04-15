package com.kata.microserviceUser.user.userDTO;

import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

import javax.xml.transform.Source;
import java.util.Objects;

@Validated
public class UserRequest {

    @NotBlank(message = "Username is obligatory")
    private String username;

    @NotBlank(message = "Name can't be blank")
    private String name;

    @NotBlank(message = "Email is obligatory")
    @Email(message = "Not valid email")
    private String email;

    @NotBlank(message = "Password is obligatory")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    public UserRequest() {
    }

    public UserRequest(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public UserRequest(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRequest that = (UserRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, email, password);
    }
}
