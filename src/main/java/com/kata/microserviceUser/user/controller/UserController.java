package com.kata.microserviceUser.user.controller;

import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.service.UserService;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequest;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long userId)
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserById(userId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> findUserByUsername(@PathVariable String username)
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserByUsername(username));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findUserByEmail(@PathVariable String email)
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAllUsers()
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest)
            throws MethodArgumentNotValidException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequest userRequest)
            throws MethodArgumentNotValidException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateUser(userId, userRequest));
    }

    @PutMapping("/password/{userId}")
    public ResponseEntity<String> updateUserPassword(@PathVariable Long userId, @Valid @RequestBody UserPasswordRequest userPasswordRequest)
            throws MethodArgumentNotValidException, ResourceNotFoundException {
            userService.updateUserPassword(userId, userPasswordRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("User with id " + userId + " password updated");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId)
            throws ResourceNotFoundException {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}