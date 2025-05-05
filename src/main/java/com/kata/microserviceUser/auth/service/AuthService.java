package com.kata.microserviceUser.auth.service;

import com.kata.microserviceUser.auth.authDTO.AuthRequest;
import com.kata.microserviceUser.auth.authDTO.AuthResponse;
import com.kata.microserviceUser.auth.security.JwtUtil;
import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.exceptions.InvalidCredentialsException;
import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest authRequest) throws ResourceNotFoundException, InvalidCredentialsException {
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException("User with email " + authRequest.getEmail() + " not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Wrong password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId(), user.isAdmin());

        return new AuthResponse(token);
    }

    public UserResponse register(UserRequestCreate authRequest) throws MethodArgumentNotValidException {
        String encodedPassword = passwordEncoder.encode(authRequest.getPassword());

        User user = new User(
                authRequest.getUsername(),
                authRequest.getName(),
                authRequest.getEmail(),
                encodedPassword,
                authRequest.isAdmin()
        );
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getUserId(), savedUser.getUsername(), savedUser.getName(), savedUser.getEmail());
    }

    public Boolean isTokenValid(String authHeader) {
        return jwtUtil.validateToken(authHeader);
    }
}
