package com.kata.microserviceUser.auth.controller;

import com.kata.microserviceUser.auth.authDTO.AuthRequest;
import com.kata.microserviceUser.auth.authDTO.AuthResponse;
import com.kata.microserviceUser.user.exceptions.InvalidCredentialsException;
import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IAuthController {

    @Operation(summary = "Login an user and get a valid token")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest
    ) throws ResourceNotFoundException, InvalidCredentialsException;

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "409", description = "Conflict")
    ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRequestCreate request
    ) throws ResourceNotFoundException, MethodArgumentNotValidException;

    @Operation(summary = "Confirm token is valid")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    ResponseEntity<Boolean> isTokenValid(
            @RequestHeader("Authorization") String authHeader
    );


}
