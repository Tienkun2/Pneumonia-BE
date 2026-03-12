package com.medical.pneumonia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.medical.pneumonia.dto.request.AuthenticationRequest;
import com.medical.pneumonia.dto.response.AuthenticationResponse;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.repository.InvalidTokenRepository;
import com.medical.pneumonia.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock UserRepository userRepository;

  @Mock InvalidTokenRepository invalidTokenRepository;

  @Mock PasswordEncoder passwordEncoder;

  @InjectMocks AuthenticationService authenticationService;

  User user;

  @BeforeEach
  void setup() {

    authenticationService.SINGER_KEY =
        "1234567890123456789012345678901234567890123456789012345678901234";
    authenticationService.VALID_DURATION = 3600;
    authenticationService.REFRESH_DURATION = 7200;

    user = User.builder().username("admin").password("encoded").build();
  }

  @Test
  void authenticate_success() {

    AuthenticationRequest request = new AuthenticationRequest();
    request.setUsername("admin");
    request.setPassword("123");

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

    when(passwordEncoder.matches("123", "encoded")).thenReturn(true);

    AuthenticationResponse response = authenticationService.Authenticated(request);

    assertTrue(response.isAuthenticated());
    assertNotNull(response.getToken());
  }

  @Test
  void authenticate_userNotFound() {

    AuthenticationRequest request = new AuthenticationRequest();
    request.setUsername("admin");

    when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> authenticationService.Authenticated(request));
  }

  @Test
  void authenticate_wrongPassword() {

    AuthenticationRequest request = new AuthenticationRequest();
    request.setUsername("admin");
    request.setPassword("123");

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

    when(passwordEncoder.matches("123", "encoded")).thenReturn(false);

    assertThrows(AppException.class, () -> authenticationService.Authenticated(request));
  }
}
