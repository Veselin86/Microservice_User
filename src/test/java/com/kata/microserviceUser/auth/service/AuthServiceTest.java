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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void login_validCredentialsGiven_returnToken() throws ResourceNotFoundException, MethodArgumentNotValidException {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";
        String token = "test_token";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        User user = new User(1L, username, "test_name", email, password, false);
        AuthRequest authRequest = new AuthRequest(email, password);

        Mockito.when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.generateToken(user.getEmail(), user.getUserId(), user.isAdmin())).thenReturn(token);

        // When
        AuthResponse actual = authService.login(authRequest);

        // Then
        assertEquals(token, actual.getToken());
    }

    @Test
    void login_givenNonExistingEmail_throwsResourceNotFoundException() throws ResourceNotFoundException, InvalidCredentialsException {
        // Given
        String email = "test@test.com";
        String password = "test1234";

        AuthRequest authRequest = new AuthRequest("t@test.com", password);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(authRequest);
        });

        // Then
        assertEquals("User with email " + authRequest.getEmail() + " not found", exception.getMessage());
    }

    @Test
    void login_givenInvalidCredentials_throwsInvalidCredentialsException() throws ResourceNotFoundException, InvalidCredentialsException {
        // Given
        String username = "test_username";
        String email = "test@test.com";
        String password = "test1234";
        User user = new User(1L, username, "test_name", email, password, false);

        AuthRequest authRequest = new AuthRequest(email, "test12");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        // When
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(authRequest);
        });

        // Then
        assertEquals("Wrong password", exception.getMessage());
    }

    @Test
    void register_validUserRequestCreateGiven_returnUserResponse() throws ResourceNotFoundException, MethodArgumentNotValidException {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        User user = new User(1L, username, "test_name", email, password, false);
        UserResponse expected = new UserResponse(1L, username, "test_name", email);
        User userToCreate = new User(username, "test_name", email, password, false);
        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name", email, password, false);
        Mockito.when(userRepository.save(userToCreate)).thenReturn(user);

        // When
        UserResponse actual = authService.register(userRequestCreate);

        // Then
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void register_validUserRequestCreateWithEmailInUse_throwDataIntegrityViolationException() throws ResourceNotFoundException, MethodArgumentNotValidException {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name", email, password, false);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("Email already taken"));

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            authService.register(userRequestCreate);
        });

        // Then
        assertEquals("Email already taken", exception.getMessage());
    }

    @Test
    void register_validUserRequestCreateWithUsernameInUse_throwDataIntegrityViolationException() throws ResourceNotFoundException, MethodArgumentNotValidException {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name", email, password, false);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("Username already taken"));

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            authService.register(userRequestCreate);
        });

        // Then
        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void isTokenValid_validTokenGiven_returnTrue() {
        // Given
        String token = "test_token";

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);

        // When
        Boolean actual = authService.isTokenValid(token);

        // Then
        assertTrue(actual);
    }

    @Test
    void isTokenValid_invalidTokenGiven_returnFalse() {
        // Given
        String token = "test_token";

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(false);

        // When
        Boolean actual = authService.isTokenValid(token);

        // Then
        assertFalse(actual);
    }
}