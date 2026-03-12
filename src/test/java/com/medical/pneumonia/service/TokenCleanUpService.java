package com.medical.pneumonia.service;

import static org.mockito.Mockito.*;

import com.medical.pneumonia.repository.InvalidTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenCleanupServiceTest {

  @Mock InvalidTokenRepository invalidTokenRepository;

  @InjectMocks TokenCleanupService tokenCleanupService;

  @Test
  void cleanupInvalidToken_success() {

    tokenCleanupService.cleanupInvalidToken();

    verify(invalidTokenRepository).deleteExpiredTokens();
  }
}
