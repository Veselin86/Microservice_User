package com.kata.microserviceUser.user.service;

import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequest;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse findUserById(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + userId + " not found")
        );

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserResponse findUserByUsername(String username) throws ResourceNotFoundException {
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

    public UserResponse findUserByEmail(String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User with email " + email + " not found")
        );

        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserResponse> findAllUsers() throws ResourceNotFoundException {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("There is no users in the database");
        }

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

    public UserResponse createUser(UserRequest userRequest) throws MethodArgumentNotValidException, ResourceNotFoundException {
        List<UserResponse> userResponse = findAllUsers();

        for (UserResponse user : userResponse) {
            if (userRequest.getUsername().equals(user.getUsername())) {
                throw new MethodArgumentNotValidException("Username is already taken");
            }
            if (userRequest.getEmail().equals(user.getEmail())) {
                throw new MethodArgumentNotValidException("Email is already taken");
            }
        }

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

    public void updateUserPassword(Long id, UserPasswordRequest userPasswordRequest)
            throws ResourceNotFoundException, MethodArgumentNotValidException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        String newPassword = userPasswordRequest.getNewPassword();
        String existingPassword = userPasswordRequest.getExistingPassword();
        String rawPassword = userPasswordRequest.getExistingPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String userPassword = user.getPassword();

        if (passwordEncoder.matches(userPasswordRequest.getExistingPassword(), user.getPassword())) {
            throw new MethodArgumentNotValidException("Wrong password");
        }

//        String rawPassword = userPasswordRequest.getNewPassword();
//        String encodedPassword = passwordEncoder.encode(rawPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    public void deleteUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        System.out.println("Trying to delete user with ID: " + id);
        userRepository.delete(user);
    }
}
