package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.UserSessionResponse;
import com.medical.pneumonia.entity.InvalidToken;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserDevice;
import com.medical.pneumonia.entity.UserSession;
import com.medical.pneumonia.enums.SessionStatus;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.UserSessionMapper;
import com.medical.pneumonia.repository.InvalidTokenRepository;
import com.medical.pneumonia.repository.UserSessionRepository;
import java.time.Instant;
import java.util.Date;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionService {

  UserSessionRepository userSessionRepository;
  InvalidTokenRepository invalidTokenRepository;
  UserSessionMapper userSessionMapper;

  public PageResponse<UserSessionResponse> getUserSessions(String userId, int page, int size) {
    var pageable = PageRequest.of(page - 1, size);
    var pageData = userSessionRepository.findByUserIdOrderByLoginTimeDesc(userId, pageable);

    return PageResponse.<UserSessionResponse>builder()
        .currentPage(page)
        .pageSize(size)
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(
            pageData.getContent().stream()
                .peek(this::checkAutoExpire)
                .map(userSessionMapper::toUserSessionResponse)
                .toList())
        .build();
  }

  private void checkAutoExpire(UserSession session) {
    if (SessionStatus.ACTIVE.equals(session.getStatus())
        && session.getExpiryTime().isBefore(Instant.now())) {
      session.setStatus(SessionStatus.EXPIRED);
      userSessionRepository.save(session);
    }
  }

  public void createSession(
      User user, UserDevice device, String jti, Instant expiry, String ua, String ip) {
    UserSession session =
        UserSession.builder()
            .user(user)
            .device(device)
            .tokenId(jti)
            .loginTime(Instant.now())
            .expiryTime(expiry)
            .userAgent(ua)
            .ipAddress(ip)
            .status(SessionStatus.ACTIVE)
            .build();
    userSessionRepository.save(session);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void revokeSession(String sessionId) {
    UserSession session =
        userSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

    if (SessionStatus.ACTIVE.equals(session.getStatus())) {
      session.setStatus(SessionStatus.REVOKED);
      userSessionRepository.save(session);

      InvalidToken invalidToken =
          InvalidToken.builder()
              .id(session.getTokenId())
              .expiryTime(Date.from(session.getExpiryTime()))
              .build();
      invalidTokenRepository.save(invalidToken);
    }
  }
}
