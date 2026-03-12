package com.medical.pneumonia.exception;

import com.medical.pneumonia.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String MIN_ATTRIBUTE = "min";

  @ExceptionHandler(value = Exception.class)
  ResponseEntity<ApiResponse> handlingException(Exception exception) {
    ApiResponse apiResponse =
        ApiResponse.builder()
            .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
            .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
            .build();
    return ResponseEntity.badRequest().body(apiResponse);
  }

  @ExceptionHandler(value = AppException.class)
  ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    ApiResponse apiResponse =
        ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();
    return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    var fieldError = exception.getFieldError();
    String enumKey =
        fieldError != null ? fieldError.getDefaultMessage() : ErrorCode.INVALID_KEY.name();
    ErrorCode errorCode = ErrorCode.INVALID_KEY;
    String message = errorCode.getMessage();
    try {
      errorCode = ErrorCode.valueOf(enumKey);

      var constraintViolation =
          exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

      var attributes = constraintViolation.getConstraintDescriptor().getAttributes();

      message = mapAttribute(errorCode.getMessage(), attributes);

      log.info("Attributes: {}", attributes.toString());

    } catch (IllegalArgumentException e) {
      errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
    }
    ApiResponse apiResponse =
        ApiResponse.builder().code(errorCode.getCode()).message(message).build();
    return ResponseEntity.badRequest().body(apiResponse);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
    ApiResponse apiResponse =
        ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();
    return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
  }

  private String mapAttribute(String message, Map<String, Object> attributes) {
    String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
    return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
  }
}
