package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.ChangePasswordRequest;
import com.medical.pneumonia.dto.request.ResendPasswordRequest;
import com.medical.pneumonia.dto.request.SetPasswordRequest;
import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.request.UserUpdateRequest;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

  UserService userService;

  @PostMapping()
  ApiResponse<UserResponse> createUser(
      @RequestBody @Valid UserCreationRequest userCreationRequest) {
    return ApiResponse.<UserResponse>builder()
        .message("User created successfully")
        .result(userService.createUser(userCreationRequest))
        .build();
  }

  @GetMapping()
  ApiResponse<PageResponse<UserResponse>> getAllUsers(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<UserResponse>>builder()
        .message("Get user list successfully")
        .result(userService.getAllUsers(page, size))
        .build();
  }

  @GetMapping("/{id}")
  ApiResponse<UserResponse> getUserById(@PathVariable String id) {
    return ApiResponse.<UserResponse>builder()
        .message("User found successfully")
        .result(userService.getUserById(id))
        .build();
  }

  @DeleteMapping("/{id}")
  ApiResponse<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ApiResponse.<Void>builder().message("User deleted successfully").build();
  }

  @PutMapping("/{id}")
  ApiResponse<UserResponse> updateUser(
      @PathVariable String id, @RequestBody UserUpdateRequest request) {
    return ApiResponse.<UserResponse>builder()
        .message("User updated successfully")
        .result(userService.updateUser(id, request))
        .build();
  }

  @GetMapping("/my-info")
  ApiResponse<UserResponse> getMyInfo() {
    return ApiResponse.<UserResponse>builder()
        .message("User info successfully")
        .result(userService.getMyInfo())
        .build();
  }

  @PostMapping("/set-password")
  ApiResponse<Void> setPassword(@Valid @RequestBody SetPasswordRequest request) {
    userService.setPassword(request.getToken(), request.getPassword());
    return ApiResponse.<Void>builder().message("Password set successfully").build();
  }

  @PostMapping("/resend-activation")
  ApiResponse<Void> resendActivation(@RequestBody ResendPasswordRequest request) {
    userService.resendActivation(request.getEmail());
    return ApiResponse.<Void>builder().message("Activation email sent successfully").build();
  }

  @PostMapping("/upload-avatar")
  ApiResponse<UserResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
    return ApiResponse.<UserResponse>builder()
        .message("Avatar uploaded successfully")
        .result(userService.uploadAvatar(file))
        .build();
  }

  @PostMapping("/change-password")
  ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    userService.changePassword(request);
    return ApiResponse.<Void>builder().message("Password changed successfully").build();
  }
}
