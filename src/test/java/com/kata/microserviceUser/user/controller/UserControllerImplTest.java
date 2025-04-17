package com.kata.microserviceUser.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserRequestUpdate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findUserById_validIdOfUser_returnUserWithResponseOK() throws Exception {
        // Given
        Long id = 1L;
        User user = new User(id, "test_username", "test_name", "test@test.com", "test1234", false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", id));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(id))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void findUserById_invalidIdOfUser_returnResponseNotFound() throws Exception {
        // Given
        Long id = 999L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", id));

        // Then
        perform.andExpect(status().isNotFound());
    }


    @Test
    void findUserByUsername_validUsernameOfUser_returnUserWithResponseOK() throws Exception {
        // Given
        String username = "test_username";
        User user = new User(1L, "test_username", "test_name", "test@test.com", "test1234", false);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/username/{username}", username));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void findUserByUsername_invalidUsernameOfUser_returnResponseNotFound() throws Exception {
        // Given
        String username = "test_username";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/username/{username}", username));

        // Then
        perform.andExpect(status().isNotFound());
    }

    @Test
    void findUserByEmail_validEmailOfUser_returnUserWithResponseOK() throws Exception {
        // Given
        String email = "test@test.com";
        User user = new User(1L, "test_username", "test_name", "test@test.com", "test1234", false);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/email/{email}", email));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void findUserByEmail_invalidEmailOfUser_returnResponseNotFound() throws Exception {
        // Given
        String email = "test@test.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/email/{email}", email));

        // Then
        perform.andExpect(status().isNotFound());
    }

    @Test
    void isAdmin_correctAdminIdGiven_returnTrue() throws Exception {
        // Given
        Long id = 1L;
        User user = new User();
        user.setUserId(id);
        user.setAdmin(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/isAdmin/{id}", id));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void isAdmin_incorrectAdminIdGiven_returnFalse() throws Exception {
        // Given
        Long id = 1L;
        User user = new User();
        user.setUserId(id);
        user.setAdmin(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/isAdmin/{id}", id));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void findAllUsers_existingUsersInDatabase_returnResponseOK() throws Exception {
        // Given
        List<User> users = new ArrayList<>(List.of(
                new User(1L, "test_username1", "test_name1", "test1@test.com", "test1234", false),
                new User(2L, "test_username2", "test_name2", "test2@test.com", "test1234", false),
                new User(3L, "test_username3", "test_name3", "test3@test.com", "test1234", false)
        ));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users"));

        // Then
        perform.andExpect(status().isOk());
    }

    @Test
    void createUser_validUserDataGiven_returnResponseCreatedWithUserResponse() throws Exception {
        // Given
        String encodedPassword = "encodedtest1234";
        User user = new User(1L, "test_username", "test_name", "test@test.com", encodedPassword, false);
        User userRequest = new User("test_username", "test_name", "test@test.com", encodedPassword, false);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedPassword);
        String json = objectMapper.writeValueAsString(user);
        Mockito.when(userRepository.save(userRequest)).thenReturn(user);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/users")
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
    void createUser_invalidUserDataGiven_returnResponseBadRequest() throws Exception {
        // Given
        User user = new User();
        String json = objectMapper.writeValueAsString(user);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/users")
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
    void createUser_validUserDataGivenEmailInUse_returnResponseConflict() throws Exception {
        // Given
        String encodedPassword = "encodedTest1234";
        String email = "test@test.com";
        UserRequestCreate userRequest = new UserRequestCreate("test_username", "test_name", email, encodedPassword, false);
        String json = objectMapper.writeValueAsString(userRequest);

        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("could not execute statement [Violación de indice de Unicidad ó Clave primaria: USERS(EMAIL NULLS FIRST)"));

        // When
        ResultActions performBAD = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .content(json));

        // Then

        performBAD.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already taken."));
    }

    @Test
    void createUser_validUserDataGivenUsernameInUse_returnResponseConflict() throws Exception {
        // Given
        String encodedPassword = "encodedTest1234";
        String username = "test_username";
        UserRequestCreate userRequest = new UserRequestCreate(username, "test_name", "test@test.com", encodedPassword, false);
        String json = objectMapper.writeValueAsString(userRequest);

        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException("could not execute statement [Violación de indice de Unicidad ó Clave primaria: USERS(USERNAME NULLS FIRST)"));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already taken."));
    }

    @Test
    void updateUser_existingIdOfUserGiven_returnResponseAccepted() throws Exception {
        // Given
        Long id = 1L;
        String username = "test_username1";
        String email = "test1@test.com";
        User user = new User(id, "test_username", "test_name", "test@test.com", "test1234", false);
        User userModified = new User(id, username, "test_name1", email, "test1234", false);

        String json = objectMapper.writeValueAsString(userModified);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(userModified)).thenReturn(userModified);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", id)
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.username").value(userModified.getUsername()))
                .andExpect(jsonPath("$.name").value(userModified.getName()))
                .andExpect(jsonPath("$.email").value(userModified.getEmail()));
    }

    @Test
    void updateUser_nonExistingIdOfUserGiven_returnResponseNotFound() throws Exception {
        // Given
        Long id = 999L;
        User user = new User(1L, "test_username", "test_name", "test@test.com", "test1234", false);

        String json = objectMapper.writeValueAsString(user);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", id)
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));
    }

    @Test
    void updateUser_existingIdEmailInUse_returnDataIntegrityViolationException() throws Exception {
        Long id = 1L;
        String email = "test@test.com";
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate("test_username1", "test_name1", email);
        String json = objectMapper.writeValueAsString(userRequestUpdate);

        Mockito.when(userRepository.findById(id))
                .thenThrow(new DataIntegrityViolationException("could not execute statement [Violación de indice de Unicidad ó Clave primaria: USERS(EMAIL NULLS FIRST)"));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", id)
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already taken."));
    }

    @Test
    void updateUser_existingIdUsernameInUse_returnDataIntegrityViolationException() throws Exception {
        Long id = 1L;
        String username = "test_username";
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate(username, "test_name1", "test@test.com");
        String json = objectMapper.writeValueAsString(userRequestUpdate);

        Mockito.when(userRepository.findById(id))
                .thenThrow(new DataIntegrityViolationException("could not execute statement [Violación de indice de Unicidad ó Clave primaria: USERS(USERNAME NULLS FIRST)"));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", id)
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already taken."));
    }

    @Test
    void updateUserPassword_existingIdGiven_passwordModified() throws Exception {
        // Given
        Long id = 1L;
        User user = new User(id, "test-username", "test_name", "test@test.com", "test1234", false);
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("test1234", "newPassword");
        String json = objectMapper.writeValueAsString(userPasswordRequest);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(userPasswordRequest.getExistingPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(passwordEncoder.encode(userPasswordRequest.getNewPassword())).thenReturn(userPasswordRequest.getNewPassword());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/users/password/{id}", id)
                .contentType("application/json")
                .content(json));

        // Then
        perform.andExpect(status().isAccepted())
                .andExpect(jsonPath("$").value("User with id " + id + " password updated"));
    }

    @Test
    void deleteUser_idOfExistingUserGiven_userDeletedNoContentReturn() throws Exception {
        // Given
        Long id = 1L;
        User user = new User(id, "test-username", "test_name", "test@test.com", "test1234", false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", id));

        // Then
        perform.andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_idOfNonExistingUserGiven_userDeletedNoContentReturn() throws Exception {
        // Given
        Long id = 999L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", id));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));
    }
}