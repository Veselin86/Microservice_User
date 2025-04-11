package com.kata.microserviceUser.user.userDTO;

import java.util.Objects;

public class UserResponse {

    private Long userId;
    private String username;
    private String name;
    private String email;

    public UserResponse() {
    }

    public UserResponse(Long userId, String username, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        UserResponse that = (UserResponse) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, name, email);
    }
}
