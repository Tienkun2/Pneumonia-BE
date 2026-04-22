package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.AuthenticationRequest;
import com.medical.pneumonia.dto.request.IntrospectRequest;
import com.medical.pneumonia.dto.request.LogoutRequest;
import com.medical.pneumonia.dto.request.RefreshRequest;
import com.medical.pneumonia.dto.response.AuthenticationResponse;
import com.medical.pneumonia.dto.response.IntrospectResponse;
import com.medical.pneumonia.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
  AuthenticationService authenticationService;

  @PostMapping("/login")
  ApiResponse<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request, HttpServletRequest servletRequest) {
    String userAgent = servletRequest.getHeader("User-Agent");
    String ipAddress = servletRequest.getRemoteAddr();

    return ApiResponse.<AuthenticationResponse>builder()
        .message("Login successfully")
        .result(authenticationService.authenticate(request, userAgent, ipAddress))
        .build();
  }

  @PostMapping("/introspect")
  ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
      throws ParseException, JOSEException {
    return ApiResponse.<IntrospectResponse>builder()
        .message("Token introspection successfully")
        .result(authenticationService.introspect(request))
        .build();
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(@RequestBody LogoutRequest request)
      throws ParseException, JOSEException {
    authenticationService.logout(request);
    return ApiResponse.<Void>builder().message("Logout successfully").build();
  }

  @PostMapping("refresh")
  ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
      throws ParseException, JOSEException {
    return ApiResponse.<AuthenticationResponse>builder()
        .message("Token refreshed successfully")
        .result(authenticationService.refreshToken(request))
        .build();
  }
}
