package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.SystemSettingRequest;
import com.medical.pneumonia.dto.request.UserSettingRequest;
import com.medical.pneumonia.dto.response.SystemSettingResponse;
import com.medical.pneumonia.dto.response.UserSettingResponse;
import com.medical.pneumonia.service.SettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingController {

  SettingService settingService;

  @GetMapping("/system")
  public ApiResponse<SystemSettingResponse> getSystemSettings() {
    return ApiResponse.<SystemSettingResponse>builder()
        .result(settingService.getSystemSettings())
        .build();
  }

  @PutMapping("/system")
  public ApiResponse<SystemSettingResponse> updateSystemSettings(
      @RequestBody SystemSettingRequest request) {
    return ApiResponse.<SystemSettingResponse>builder()
        .result(settingService.updateSystemSettings(request))
        .build();
  }

  @GetMapping("/user")
  public ApiResponse<UserSettingResponse> getUserSettings() {
    return ApiResponse.<UserSettingResponse>builder()
        .result(settingService.getUserSettings())
        .build();
  }

  @PutMapping("/user")
  public ApiResponse<UserSettingResponse> updateUserSettings(
      @RequestBody UserSettingRequest request) {
    return ApiResponse.<UserSettingResponse>builder()
        .result(settingService.updateUserSettings(request))
        .build();
  }
}
