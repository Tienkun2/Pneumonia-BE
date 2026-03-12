package com.medical.pneumonia.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
      throws IOException, ServletException {

    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

    ApiResponse apiResponse =
        ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();

    response.setStatus(errorCode.getHttpStatusCode().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ObjectMapper objectMapper = new ObjectMapper();
    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    response.flushBuffer();
  }
}
