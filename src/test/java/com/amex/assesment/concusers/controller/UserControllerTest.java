package com.amex.assesment.concusers.controller;

import com.amex.assesment.concusers.model.User;
import com.amex.assesment.concusers.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    void createUser_withValidData_returnsCreated() throws Exception {
        User user = new User(0, "New User", "newuser@example.com");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    void createUser_withInvalidData_returnsBadRequest() throws Exception {
        User user = new User(0, "", "invalid-email"); // Invalid name and email

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withDuplicateEmail_returnsConflict() throws Exception {
        User user = new User(0, "New User", "newuser@example.com");
        userService.createUser(user);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_whenUserExists_returnsUser() throws Exception {
        User createdUser = userService.createUser(new User(0, "Test User", "test@example.com"));

        mockMvc.perform(get("/api/v1/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_whenUserExists_returnsUpdatedUser() throws Exception {
        User createdUser = userService.createUser(new User(0, "Original Name", "original@example.com"));
        User userDetails = new User(0, "Updated Name", "updated@example.com");

        mockMvc.perform(put("/api/v1/users/{id}", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateUser_whenUserDoesNotExist_returnsNotFound() throws Exception {
        User userDetails = new User(0, "Updated Name", "updated@example.com");

        mockMvc.perform(put("/api/v1/users/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_whenUserExists_returnsNoContent() throws Exception {
        User createdUser = userService.createUser(new User(0, "To Be Deleted", "delete@example.com"));

        mockMvc.perform(delete("/api/v1/users/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenUserDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_whenUsersExist_returnsUsers() throws Exception {
        userService.createUser(new User(0, "User 1", "user1@example.com"));
        userService.createUser(new User(0, "User 2", "user2@example.com"));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllUsers_whenNoUsersExist_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
