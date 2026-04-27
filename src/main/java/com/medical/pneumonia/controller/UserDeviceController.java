package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.response.UserDeviceResponse;
import com.medical.pneumonia.service.UserDeviceService;
import com.medical.pneumonia.service.UserService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-devices")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDeviceController {

  UserDeviceService userDeviceService;
  UserService userService;

  @GetMapping("/my-devices")
  public ApiResponse<List<UserDeviceResponse>> getMyDevices(@AuthenticationPrincipal Jwt jwt) {
    String currentUserId = userService.getMyInfo().getId();
    String currentDeviceId = jwt.getClaimAsString("did");
    return ApiResponse.<List<UserDeviceResponse>>builder()
        .message("Get your login devices successfully")
        .result(userDeviceService.getMyDevices(currentUserId, currentDeviceId))
        .build();
  }

  @GetMapping("/user/{userId}")
  public ApiResponse<List<UserDeviceResponse>> getUserDevices(
      @PathVariable String userId, @AuthenticationPrincipal Jwt jwt) {
    String currentDeviceId = jwt.getClaimAsString("did");
    return ApiResponse.<List<UserDeviceResponse>>builder()
        .message("Get user login devices successfully")
        .result(userDeviceService.getUserDevices(userId, currentDeviceId))
        .build();
  }

  @PatchMapping("/{deviceId}/revoke")
  public ApiResponse<Void> revokeDevice(
      @PathVariable String deviceId, @AuthenticationPrincipal Jwt jwt) {
    userDeviceService.revokeDevice(deviceId, jwt.getId());
    return ApiResponse.<Void>builder().message("Device access revoked successfully").build();
  }

  @DeleteMapping("/{deviceId}")
  public ApiResponse<Void> deleteDevice(@PathVariable String deviceId) {
    userDeviceService.deleteDevice(deviceId);
    return ApiResponse.<Void>builder().message("Device record deleted successfully").build();
  }

  @PatchMapping("/{deviceId}/trust")
  public ApiResponse<Void> trustDevice(@PathVariable String deviceId) {
    userDeviceService.trustDevice(deviceId);
    return ApiResponse.<Void>builder().message("Device trusted successfully").build();
  }
}
