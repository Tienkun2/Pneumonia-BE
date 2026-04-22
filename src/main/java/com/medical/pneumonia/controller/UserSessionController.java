package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.UserSessionResponse;
import com.medical.pneumonia.service.UserSessionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-sessions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionController {

  UserSessionService userSessionService;

  @GetMapping("/user/{userId}")
  public ApiResponse<PageResponse<UserSessionResponse>> getUserSessions(
      @PathVariable String userId,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<UserSessionResponse>>builder()
        .message("Get user sessions successfully")
        .result(userSessionService.getUserSessions(userId, page, size))
        .build();
  }

  @DeleteMapping("/{sessionId}")
  public ApiResponse<Void> revokeSession(@PathVariable String sessionId) {
    userSessionService.revokeSession(sessionId);
    return ApiResponse.<Void>builder().message("Session revoked successfully").build();
  }
}
