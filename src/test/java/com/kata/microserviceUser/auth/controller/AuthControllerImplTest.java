package com.kata.microserviceUser.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.microserviceUser.auth.authDTO.AuthRequest;
import com.kata.microserviceUser.auth.authDTO.AuthResponse;
import com.kata.microserviceUser.auth.security.JwtUtil;
import com.kata.microserviceUser.auth.service.AuthService;
import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.exceptions.InvalidCredentialsException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.service.UserService;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Test
    void login_validCredentialsGiven_returnResponse200AndToken() throws Exception {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";
        String token = "test_token";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        User user = new User(1L, username, "test_name", email, password, false);
        AuthRequest authRequest = new AuthRequest(email, password);
        String json = objectMapper.writeValueAsString(authRequest);

        AuthResponse authResponse = new AuthResponse(token);
        Mockito.when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.generateToken(user.getEmail(), user.getUserId(), user.isAdmin())).thenReturn(token);
        Mockito.when(authService.login(Mockito.any())).thenReturn(authResponse);

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.post("/auth/login"))
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()));
    }

    @Test
    void login_invalidCredentialsGivenNonExistingEmail_throwResourceNotFoundException() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "test1234";

        AuthRequest authRequest = new AuthRequest(email, password);
        String json = objectMapper.writeValueAsString(authRequest);

        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(authService.login(Mockito.any())).thenThrow(new ResourceNotFoundException("User with email " + authRequest.getEmail() + " not found"));

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.post("/auth/login"))
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with email " + authRequest.getEmail() + " not found"));
    }

    @Test
    void login_invalidCredentialsGivenNoPasswordMatch_throwInvalidCredentialsException() throws Exception {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        User user = new User(1L, username, "test_name", email, password, false);
        AuthRequest authRequest = new AuthRequest(email, "test12");
        String json = objectMapper.writeValueAsString(authRequest);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);
        Mockito.when(authService.login(Mockito.any()))
                .thenThrow(new InvalidCredentialsException("Wrong password"));

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.post("/auth/login"))
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Wrong password"));
    }


    @Test
    void register_validUserRequestGiven_returnResponseCreated() throws Exception {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);
        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name", email, password, false);
        String json = objectMapper.writeValueAsString(userRequestCreate);

        User user = new User(1L, username, "test_name", email, password, false);
        UserResponse userResponse = new UserResponse(1L, username, "test_name", email);
        User userToCreate = new User(username, "test_name", email, password, false);
        Mockito.when(userRepository.save(userToCreate)).thenReturn(user);
        Mockito.when(authService.register(Mockito.any())).thenReturn(userResponse);

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.post("/auth/register"))
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void register_invalidUserRequestGiven_throwMethodArgumentNotValidException() throws Exception {
        // Given

        UserRequestCreate userRequestCreate = new UserRequestCreate();
        String json = objectMapper.writeValueAsString(userRequestCreate);

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.post("/auth/register"))
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password is obligatory"))
                .andExpect(jsonPath("$.name").value("Name can't be blank"))
                .andExpect(jsonPath("$.email").value("Email is obligatory"))
                .andExpect(jsonPath("$.username").value("Username is obligatory"));
    }

    @Test
    void register_validUserRequestGivenEmailInUse_throwMethodArgumentNotValidException() throws Exception {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);
        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name", email, password, false);
        String json = objectMapper.writeValueAsString(userRequestCreate);

        User user = new User(1L, username, "test_name", email, password, false);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(password);
        Mockito.when(authService.register(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("could not execute statement [Violación de indice de Unicidad ó Clave primaria: USERS(EMAIL NULLS FIRST)"));

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.post("/auth/register"))
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already taken."));
    }

    @Test
    void isTokenValid_validTokenGiven_returnTrue() throws Exception {
        // Given
        String token = "test_token";

        Mockito.when(jwtUtil.validateToken(Mockito.any())).thenReturn(true);
        Mockito.when(authService.isTokenValid(Mockito.any())).thenReturn(true);

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.get("/auth/validate"))
                .header("Authorization", token));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void isTokenValid_validTokenFalse_returnFalse() throws Exception {
        // Given
        String token = "test_token";

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(false);
        Mockito.when(authService.isTokenValid(token)).thenReturn(false);

        // When
        ResultActions perform = mockMvc.perform((MockMvcRequestBuilders.get("/auth/validate"))
                .header("Authorization", token));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}