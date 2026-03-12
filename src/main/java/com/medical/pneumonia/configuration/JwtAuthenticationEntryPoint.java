package com.medical.pneumonia.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
    response.setStatus(errorCode.getHttpStatusCode().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiResponse apiResponse =
        ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();

    ObjectMapper objectMapper = new ObjectMapper();
    // Conver object to json
    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    response.flushBuffer();
  }
}
