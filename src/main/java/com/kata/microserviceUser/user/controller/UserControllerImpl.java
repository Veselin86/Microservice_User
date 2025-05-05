package com.kata.microserviceUser.user.controller;

import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.exceptions.UnauthotizedException;
import com.kata.microserviceUser.user.service.UserService;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserRequestUpdate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserControllerImpl implements IUserController {

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
    public ResponseEntity<UserResponse> findUserByEmail(@Valid @PathVariable String email)
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserByEmail(email));
    }

    @GetMapping("/isAdmin/{userId}")
    public ResponseEntity<Boolean> isAdmin(@PathVariable Long userId)
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.isAdmin(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAllUsers()
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsers());
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequestCreate userRequestCreate) throws UnauthotizedException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestCreate));
    }

    @PutMapping("/{userId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequestUpdate userRequestUpdate)
            throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.updateUser(userId, userRequestUpdate));
    }

    @PutMapping("/password/{userId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> updateUserPassword(@PathVariable Long userId, @Valid @RequestBody UserPasswordRequest userPasswordRequest)
            throws MethodArgumentNotValidException, ResourceNotFoundException {
        userService.updateUserPassword(userId, userPasswordRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("User with id " + userId + " password updated");
    }

    @DeleteMapping("/{userId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId)
            throws ResourceNotFoundException, UnauthotizedException {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}