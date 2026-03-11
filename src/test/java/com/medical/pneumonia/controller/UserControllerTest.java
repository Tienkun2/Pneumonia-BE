package com.medical.pneumonia.controller;

import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.pneumonia.configuration.CustomJwtDecoder;
import com.medical.pneumonia.configuration.SecurityConfig;
import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.service.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomJwtDecoder customJwtDecoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserCreationRequest request;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        request = UserCreationRequest.builder()
                .username("test1231")
                .password("test1234")
                .build();

        response = UserResponse.builder()
                .username("test1231")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {

        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(response);

        mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.result.username").value("test1231"));

        Mockito.verify(userService).createUser(ArgumentMatchers.any());
    }

    @Test
    void createUser_userNameLengthFailed_failed() throws Exception {

        request.setUsername("test");

        mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4004"))
                .andExpect(jsonPath("$.message").value("Username must be at least 6 characters long"))
                .andDo(print());
        // If validate failed, not call service
        Mockito.verify(userService, never()).createUser(ArgumentMatchers.any());
    }

    @Test
    void createUser_passwordLengthFailed_failed() throws Exception{
        request.setPassword("test");

        mockMvc.perform(post("/users")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("4005"))
            .andExpect(jsonPath("$.message").value("Password must be at least 6 characters long"))
            .andDo(print());
        // If validate failed, not call service
        Mockito.verify(userService, never()).createUser(ArgumentMatchers.any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getListUser_success() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value("Get user list successfully"))
                .andExpect(jsonPath("$.result").isArray())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getListUser_failed() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("1002"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"))
                .andDo(print());
    }
}