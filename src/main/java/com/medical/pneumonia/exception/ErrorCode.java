package com.medical.pneumonia.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_KEY(9998, "Invalid key", HttpStatus.BAD_REQUEST),
  // Authentication
  UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED(1002, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
  // Permission
  PERMISSION_NOT_FOUND(2001, "Permisson not found", HttpStatus.NOT_FOUND),
  // Role
  ROLE_NOT_FOUND(3001, "Role not found", HttpStatus.NOT_FOUND),
  // User
  USER_EXISTED(4001, "User already exists", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND(4002, "User not found", HttpStatus.NOT_FOUND),
  USER_NOT_EXISTED(4003, "User not existed", HttpStatus.NOT_FOUND),
  USERNAME_INVALID(4004, "Username must be at least {min} characters long", HttpStatus.BAD_REQUEST),
  PASSWORD_INVALID(4005, "Password must be at least {min} characters long", HttpStatus.BAD_REQUEST),
  DOB_INVALID(4006, "You must be at least {min} years old", HttpStatus.BAD_REQUEST),
  INVALID_ACTIVATION_TOKEN(4007, "Invalid activation token", HttpStatus.BAD_REQUEST),
  ACTIVATION_TOKEN_EXPIRED(4008, "Activation token has expired", HttpStatus.BAD_REQUEST),
  USER_ALREADY_ACTIVE(4009, "User is already active", HttpStatus.BAD_REQUEST),
  OLD_PASSWORD_INCORRECT(4010, "Old password incorrect", HttpStatus.BAD_REQUEST),

  // Patient
  PATIENT_EXISTED(5001, "Patient already exists", HttpStatus.BAD_REQUEST),
  PATIENT_NOT_FOUND(5002, "Patient not found", HttpStatus.NOT_FOUND),

  // Visit
  VISIT_NOT_FOUND(6001, "Visit not found", HttpStatus.NOT_FOUND),
  ;

  ErrorCode(int code, String message, HttpStatus httpStatusCode) {
    this.code = code;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
  }

  private int code;
  private String message;
  private HttpStatus httpStatusCode;
}
