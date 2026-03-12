package com.medical.pneumonia.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.pneumonia.configuration.CustomJwtDecoder;
import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.repository.RoleRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserControllerIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
      new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @MockBean private CustomJwtDecoder customJwtDecoder;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private UserCreationRequest request;

  @Autowired private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {

    if (!roleRepository.existsById("USER")) {
      Role role = Role.builder().name("USER").build();
      roleRepository.save(role);
    }

    request =
        UserCreationRequest.builder()
            .username("test123456")
            .password("test123456")
            .dob(LocalDate.of(2000, 10, 1))
            .build();
  }

  @DynamicPropertySource
  static void configureDatabase(DynamicPropertyRegistry registry) {

    registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    registry.add("spring.datasource.driver-class-name", POSTGRES_CONTAINER::getDriverClassName);
  }

  @Test
  void createUser_validRequest_success() throws Exception {

    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.result.username").value("test123456"));
  }

  @Test
  void createUser_userNameLengthFailed_failed() throws Exception {

    request.setUsername("test");

    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("4004"))
        .andDo(print());
  }

  @Test
  void createUser_passwordLengthFailed_failed() throws Exception {

    request.setPassword("123");

    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("4005"))
        .andDo(print());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void getListUser_success() throws Exception {

    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.result").isArray())
        .andDo(print());
  }

  @Test
  @WithMockUser(roles = "USER")
  void getListUser_failed() throws Exception {

    mockMvc
        .perform(get("/users"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("1002"))
        .andDo(print());
  }
}
