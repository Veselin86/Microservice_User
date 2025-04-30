package com.kata.microserviceUser.user.service;

import com.kata.microserviceUser.user.entity.User;
import com.kata.microserviceUser.user.exceptions.MethodArgumentNotValidException;
import com.kata.microserviceUser.user.exceptions.ResourceNotFoundException;
import com.kata.microserviceUser.user.repository.UserRepository;
import com.kata.microserviceUser.user.userDTO.UserPasswordRequest;
import com.kata.microserviceUser.user.userDTO.UserRequestCreate;
import com.kata.microserviceUser.user.userDTO.UserRequestUpdate;
import com.kata.microserviceUser.user.userDTO.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findUserById_existingId_returnUserResponse() throws ResourceNotFoundException {
        // Given
        Long id = 1L;
        User user = new User(id, "test_username", "test_name", "test@test.com", "test1234", false);
        UserResponse expected = new UserResponse(id, "test_username", "test_name", "test@test.com");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        UserResponse actual = userService.findUserById(id);

        // Then
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    void findUserById_unExistingUsername_throwsResourceNotFound() throws ResourceNotFoundException {
        // Given
        Long id = 999L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(id);
        });

        // Then
        assertEquals("User with id " + id + " not found", exception.getMessage());
    }

    @Test
    void findUserByUsername_existingUsername_returnUserResponse() throws ResourceNotFoundException {
        // Given
        Long id = 1L;
        String username = "test_username";
        User user = new User(id, "test_username", "test_name", "test@test.com", "test1234", false);
        UserResponse expected = new UserResponse(id, "test_username", "test_name", "test@test.com");
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserResponse actual = userService.findUserByUsername(username);

        // Then
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    void findUserByUsername_nonExistingUsername_throwsResourceNotFound() throws ResourceNotFoundException {
        // Given
        String username = "nonexistigUsername";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserByUsername(username);
        });

        // Then
        assertEquals("User with username " + username + " not found", exception.getMessage());
    }

    @Test
    void findUserByEmail_existingEmail_returnUserResponse() throws ResourceNotFoundException {
        // Given
        Long id = 1L;
        String email = "test@test.com";
        User user = new User(id, "test_username", "test_name", "test@test.com", "test1234", false);
        UserResponse expected = new UserResponse(id, "test_username", "test_name", "test@test.com");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserResponse actual = userService.findUserByEmail(email);

        // Then
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    void findUserByEmail_nonExistingEmail_throwsResourceNotFound() throws ResourceNotFoundException {
        // Given
        String email = "nonexistig@email.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserByEmail(email);
        });

        // Then
        assertEquals("User with email " + email + " not found", exception.getMessage());
    }

    @Test
    void isAdmin_correctAdminIdGiven_returnTrue() throws ResourceNotFoundException {
        // Given
        Long id = 1L;
        User user = new User();
        user.setUserId(id);
        user.setAdmin(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        boolean actual = userService.isAdmin(id);

        // Then
        assertTrue(actual);
    }

    @Test
    void isAdmin_incorrectAdminIdGiven_returnFalse() throws ResourceNotFoundException {
        // Given
        Long id = 1L;
        User user = new User();
        user.setUserId(id);
        user.setAdmin(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        boolean actual = userService.isAdmin(id);

        // Then
        assertFalse(actual);
    }

    @Test
    void isAdmin_inexistingIdGiven_throwsResourceNotFound() throws ResourceNotFoundException {
        // Given
        Long id = 999L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.isAdmin(id);
        });

        // Then
        assertEquals("User with id " + id + " not found", exception.getMessage());
    }

    @Test
    void findAllUsers_existingUsersInDatabase_returnListOfUserResponse() throws ResourceNotFoundException {
        // Given
        List<User> users = new ArrayList<>(List.of(
                new User(1L, "test_username1", "test_name1", "test1@test.com", "test1234", false),
                new User(2L, "test_username2", "test_name2", "test2@test.com", "test1234", false),
                new User(3L, "test_username3", "test_name3", "test3@test.com", "test1234", false)
        ));
        List<UserResponse> expected = new ArrayList<>(List.of(
                new UserResponse(1L, "test_username1", "test_name1", "test1@test.com"),
                new UserResponse(2L, "test_username2", "test_name2", "test2@test.com"),
                new UserResponse(3L, "test_username3", "test_name3", "test3@test.com")
        ));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserResponse> actual = userService.findAllUsers();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createUser_validUserRequestGiven_returnUserResponse() throws MethodArgumentNotValidException, ResourceNotFoundException {
        // Given
        String email = "test@test.com";
        String username = "test_username";
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        User user = new User(1L, username, "test_name", email, password, false);
        UserResponse expected = new UserResponse(1L, username, "test_name", email);
        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name", email, password, false);
        User admin = new User(2L, "admin_username", "admin_name", "admin@test.com", "admin1234", true);
        Mockito.when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
//        Mockito.when(userRepository.isAdmin(admin.getUserId())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        UserResponse actual = userService.createUser(admin.getUserId(), userRequestCreate);

        // Then
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void createUser_validUserRequestWithEmailInUse_throwDataIntegrityViolationException() {
        // Given
        String email = "test@test.com";
        UserRequestCreate userRequestCreate = new UserRequestCreate("test_username1", "test_name1", email, "test1234", false);
        User admin = new User(2L, "admin_username", "admin_name", "admin@test.com", "admin1234", true);
        Mockito.when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
//        Mockito.when(userRepository.isAdmin(admin.getUserId())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Email already taken"));

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.createUser(admin.getUserId(), userRequestCreate);
        });

        // Then
        assertEquals("Email already taken", exception.getMessage());
    }

    @Test
    void createUser_validUserRequestWithUsernameInUse_throwUniqueFieldException() {
        // Given
        String username = "test_username";
        UserRequestCreate userRequestCreate = new UserRequestCreate(username, "test_name1", "test1@test.com", "test1234", false);
        User admin = new User(2L, "admin_username", "admin_name", "admin@test.com", "admin1234", true);
        Mockito.when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
//        Mockito.when(userRepository.isAdmin(admin.getUserId())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Username already taken"));

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.createUser(admin.getUserId(), userRequestCreate);
        });

        // Then
        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void updateUser_validUserRequest_returnUserResponseOfModifiedUser() throws ResourceNotFoundException {
        // Given
        Long id = 1L;
        String password = "test1234";

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn(password);

        User user = new User(id, "test_username", "test_name", "test@test.com", password, false);
        User modifiedUser = new User();
        modifiedUser.setUsername("test_username1");
        modifiedUser.setName("test_name1");
        modifiedUser.setEmail("test1@test.com");
        UserResponse expected = new UserResponse(id, "test_username1", "test_name1", "test1@test.com");
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate("test_username1", "test_name1", "test1@test.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(modifiedUser);

        // When
        UserResponse actual = userService.updateUser(id, userRequestUpdate);

        // Then
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    void updateUser_validUserRequestWithEmailInUse_throwUniqueFieldException() {
        // Given
        Long id = 1L;
        String email = "test@test.com";
        User user = new User(id, "test_username", "test_name", email, "test1234", false);
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate("test_username1", "test_name1", email);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Email already taken"));

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.updateUser(id, userRequestUpdate);
        });

        // Then
        assertEquals("Email already taken", exception.getMessage());
    }

    @Test
    void updateUser_validUserRequestWithUsernameInUse_throwUniqueFieldException() {
        // Given
        Long id = 1L;
        String username = "test_username";
        User userExisting = new User(1L, username, "test_name", "test@test.com", "test1234", false);
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate(username, "test_name1", "test1@test.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userExisting));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Username already taken"));

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userService.updateUser(id, userRequestUpdate);
        });

        // Then
        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void updateUserPassword_validUserPasswordRequestGiven_returnPasswordModified() throws ResourceNotFoundException, MethodArgumentNotValidException {
        // Given
        Long id = 1L;
        String password = "test1234";
        String newPassword = "newPassword";
        Mockito.when(passwordEncoder.encode(password)).thenReturn(password);
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setExistingPassword(password);
        userPasswordRequest.setNewPassword(newPassword);

        User user = new User(id, "test_username", "test_name", "test@test.com", password, false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode(userPasswordRequest.getNewPassword())).thenReturn(newPassword);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        // When
        userService.updateUserPassword(id, userPasswordRequest);

        // Then
        assertEquals(user.getPassword(), userPasswordRequest.getNewPassword());
    }

    @Test
    void updateUserPassword_invalidUserPasswordRequestGiven_returnExceptionWrongPassword() throws ResourceNotFoundException, MethodArgumentNotValidException {
        // Given
        Long id = 1L;
        String existPassword = "test1234";
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword";
        Mockito.when(passwordEncoder.encode(existPassword)).thenReturn(existPassword);
        Mockito.when(passwordEncoder.encode(wrongPassword)).thenReturn(wrongPassword);
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setExistingPassword(wrongPassword);
        userPasswordRequest.setNewPassword(newPassword);

        User user = new User(id, "test_username", "test_name", "test@test.com", existPassword, false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode(userPasswordRequest.getNewPassword())).thenReturn(wrongPassword);
        Mockito.when(passwordEncoder.matches(existPassword, user.getPassword())).thenReturn(false);

        // When
        MethodArgumentNotValidException exception = assertThrows(MethodArgumentNotValidException.class, () -> {
            userService.updateUserPassword(id, userPasswordRequest);
        });

        // Then
        assertEquals(existPassword, user.getPassword());
        assertTrue(exception.getMessage().contains("Wrong password"));
    }

//    @Test
//    void deleteUser_validIdGiven_userDeleted() throws ResourceNotFoundException {
//        // Given
//        Long id = 1L;
//        User user = new User(id, "test_username", "test_name", "test@test.com", "test1234", false);
//        User admin = new User(2L, "admin_username", "admin_name", "admin@test.com", "admin1234", true);
//        Mockito.when(userRepository.findById(admin.getUserId())).thenReturn(Optional.of(admin));
//        Mockito.when(userRepository.isAdmin(admin.getUserId())).thenReturn(true);
//        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
//
//        // When
//        userService.deleteUser(admin.getUserId(), id);
//        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
//    }
}