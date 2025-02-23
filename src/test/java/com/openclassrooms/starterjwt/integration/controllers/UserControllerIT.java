package com.openclassrooms.starterjwt.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testFindById_UserExists() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user = userRepository.save(user);

        mockMvc.perform(get("/api/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testFindById_UserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testDeleteUser_Success() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/user/" + user.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testDeleteUser_Unauthorized() throws Exception {
        User user = new User();
        user.setEmail("other@test.com");
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/user/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void testDeleteUser_UserDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNotFound());
    }
}