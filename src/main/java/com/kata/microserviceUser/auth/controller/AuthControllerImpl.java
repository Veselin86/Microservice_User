package com.kata.microserviceUser.auth.controller;

import com.kata.microserviceUser.auth.authDTO.AuthRequest;
import com.kata.microserviceUser.auth.authDTO.AuthResponse;
import com.kata.microserviceUser.auth.service.AuthService;
import com.kata.microserviceUser.user.exceptions.InvalidCredentialsException;
import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements IAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) throws ResourceNotFoundException, InvalidCredentialsException {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequestCreate request) throws ResourceNotFoundException, MethodArgumentNotValidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> isTokenValid(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.isTokenValid(authHeader));
    }
}
