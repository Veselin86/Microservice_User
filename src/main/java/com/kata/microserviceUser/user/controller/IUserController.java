package com.kata.microserviceUser.user.controller;

import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserRequestUpdate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IUserController {

    @Operation(summary = "Get user information by his ID")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    ResponseEntity<UserResponse> findUserById(
            @PathVariable Long userId
    ) throws ResourceNotFoundException;

    @Operation(summary = "Get user information by his username")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    ResponseEntity<UserResponse> findUserByUsername(
            @PathVariable String username
    ) throws ResourceNotFoundException;

    @Operation(summary = "Get user information by his email")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    ResponseEntity<UserResponse> findUserByEmail(
            @Valid @PathVariable String email
    ) throws ResourceNotFoundException;

    @Operation(summary = "Get a list of users")
    @ApiResponse(responseCode = "200", description = "Success")
    ResponseEntity<List<UserResponse>> findAllUsers() throws ResourceNotFoundException;

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "409", description = "Conflict")
    ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequestCreate userRequestCreate
    );

    @Operation(summary = "Update an existing user")
    @ApiResponse(responseCode = "201", description = "Accepted")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "409", description = "Conflict")
    ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestUpdate userRequestUpdate
    ) throws ResourceNotFoundException;

    @Operation(summary = "Update an existing user's password")
    @ApiResponse(responseCode = "202", description = "Accepted")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    ResponseEntity<String> updateUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody UserPasswordRequest userPasswordRequest
    ) throws MethodArgumentNotValidException, ResourceNotFoundException;

    @Operation(summary = "Delete an existing user")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not Found")
    ResponseEntity<Object> deleteUser(
            @PathVariable Long userId
    ) throws ResourceNotFoundException;
}
