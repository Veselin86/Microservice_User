package com.kata.microserviceUser.auth.authDTO;

public class AuthResponse {

    private String email;
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(String email, String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
