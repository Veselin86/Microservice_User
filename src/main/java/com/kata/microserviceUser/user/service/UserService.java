package com.kata.microserviceUser.user.service;

import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserRequestUpdate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static UserResponse getUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserResponse findUserById(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + userId + " not found")
        );

        return getUserResponse(user);
    }

    public UserResponse findUserByUsername(String username) throws ResourceNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User with username " + username + " not found")
        );

        return getUserResponse(user);
    }

    public UserResponse findUserByEmail(String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User with email " + email + " not found")
        );

        return getUserResponse(user);
    }

    public Boolean isAdmin(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + userId + " not found")
        );

        return user.isAdmin();
    }

    public List<UserResponse> findAllUsers() throws ResourceNotFoundException {
        List<User> users = userRepository.findAll();

        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : users) {
            UserResponse userResponse = getUserResponse(user);
            userResponses.add(userResponse);
        }

        return userResponses;
    }

    public UserResponse createUser(UserRequestCreate userRequestCreate) {
        String rawPassword = userRequestCreate.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(
                userRequestCreate.getUsername(),
                userRequestCreate.getName(),
                userRequestCreate.getEmail(),
                encodedPassword,
                userRequestCreate.isAdmin()
        );

        user = userRepository.save(user);

        return getUserResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        user.setUsername(userRequestUpdate.getUsername());
        user.setName(userRequestUpdate.getName());
        user.setEmail(userRequestUpdate.getEmail());

        userRepository.save(user);

        return getUserResponse(user);
    }

    public void updateUserPassword(Long id, UserPasswordRequest userPasswordRequest)
            throws ResourceNotFoundException, MethodArgumentNotValidException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (!passwordEncoder.matches(userPasswordRequest.getExistingPassword(), user.getPassword())) {
            throw new MethodArgumentNotValidException("Wrong password");
        }

        String rawPassword = userPasswordRequest.getNewPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    public void deleteUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.delete(user);
    }
}
