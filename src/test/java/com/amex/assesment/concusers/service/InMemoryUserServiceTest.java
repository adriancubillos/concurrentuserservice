package com.amex.assesment.concusers.service;

import com.amex.assesment.concusers.exception.UserNotFoundException;
import com.amex.assesment.concusers.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserService();
    }

    @Test
    void createUser() {
        User user = new User(0, "Test User", "test@example.com");
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertTrue(createdUser.getId() > 0);
        assertEquals("Test User", createdUser.getName());
    }

    @Test
    void getUserById_whenUserExists() {
        User user = new User(0, "Test User", "test@example.com");
        User createdUser = userService.createUser(user);
        User foundUser = userService.getUserById(createdUser.getId());
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    void getUserById_whenUserDoesNotExist_thenThrowException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void getAllUsers() {
        userService.createUser(new User(0, "User 1", "user1@example.com"));
        userService.createUser(new User(0, "User 2", "user2@example.com"));
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void updateUser_whenUserExists() {
        User createdUser = userService.createUser(new User(0, "Original Name", "original@example.com"));
        User userDetails = new User(0, "Updated Name", "updated@example.com");

        User updatedUser = userService.updateUser(createdUser.getId(), userDetails);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_whenUserDoesNotExist_thenThrowException() {
        User userDetails = new User(0, "Updated Name", "updated@example.com");
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(999L, userDetails);
        });
    }

    @Test
    void deleteUser_whenUserExists() {
        User createdUser = userService.createUser(new User(0, "Test User", "test@example.com"));
        assertDoesNotThrow(() -> userService.deleteUser(createdUser.getId()));
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(createdUser.getId());
        });
    }

    @Test
    void deleteUser_whenUserDoesNotExist_thenThrowException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }
}
