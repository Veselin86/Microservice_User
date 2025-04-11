package com.kata.microserviceUser.user.service;

import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequest;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse findUserById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found")
        );

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserResponse findUserByUsername(UserRequest userRequest) throws ResourceNotFoundException {
        String username = userRequest.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User with username " + username + " not found")
        );

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserResponse findUserByEmail(UserRequest userRequest) throws ResourceNotFoundException {
        String email = userRequest.getEmail();
        User user = userRepository.findByUsername(email).orElseThrow(
                () -> new ResourceNotFoundException("User with email " + email + " not found")
        );

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserResponse> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : users) {
            UserResponse userResponse = new UserResponse(
                    user.getUserId(),
                    user.getUsername(),
                    user.getName(),
                    user.getEmail()
            );
            userResponses.add(userResponse);
        }

        return userResponses;
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        String rawPassword = userRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        user.setUsername(userRequest.getUsername());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(encodedPassword);

        User newUser = userRepository.save(user);

        return new UserResponse(
                newUser.getUserId(),
                newUser.getUsername(),
                newUser.getName(),
                newUser.getEmail()
        );
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        user.setUsername(userRequest.getUsername());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        userRepository.save(user);

        return new UserResponse(
                    id,
                    userRequest.getUsername(),
                    userRequest.getName(),
                    userRequest.getEmail()
        );
    }

    public UserResponse updateUserPassword(Long id, UserPasswordRequest userPasswordRequest) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        String rawPassword = userPasswordRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public Long deleteUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.delete(user);
        return user.getUserId();
    }
}
