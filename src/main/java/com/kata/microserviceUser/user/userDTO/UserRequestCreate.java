package com.kata.microserviceUser.user.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Validated
public class UserRequestCreate {

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

    private boolean isAdmin = false;

    public UserRequestCreate() {
    }

    public UserRequestCreate(String username, String name, String email, boolean isAdmin) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public UserRequestCreate(String username, String name, String email, String password, boolean isAdmin) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRequestCreate that = (UserRequestCreate) o;
        return isAdmin == that.isAdmin && Objects.equals(username, that.username) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, email, password, isAdmin);
    }
}
