package com.medical.pneumonia.service;

import com.medical.pneumonia.repository.InvalidTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

  private final InvalidTokenRepository invalidTokenRepository;

  @Scheduled(cron = "0 0 2 * * *")
  public void cleanupInvalidToken() {
    log.info("Cleaning expired tokens...");
    invalidTokenRepository.deleteExpiredTokens();
  }
}
