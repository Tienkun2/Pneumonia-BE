package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.PermissionCreationRequest;
import com.medical.pneumonia.dto.response.PermissionResponse;
import com.medical.pneumonia.service.PermissionService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/permissions")
public class PermissionController {

  PermissionService permissionService;

  @PostMapping()
  ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionCreationRequest request) {
    return ApiResponse.<PermissionResponse>builder()
        .message("Create permission successfully")
        .result(permissionService.createPermission(request))
        .build();
  }

  @GetMapping()
  ApiResponse<List<PermissionResponse>> getListPermission() {
    return ApiResponse.<List<PermissionResponse>>builder()
        .message("Get list permission successfully")
        .result(permissionService.getAllPermissions())
        .build();
  }

  @GetMapping("/{name}")
  ApiResponse<PermissionResponse> getPermission(@PathVariable String name) {
    return ApiResponse.<PermissionResponse>builder()
        .result(permissionService.getPermission(name))
        .build();
  }

  @DeleteMapping("/{name}")
  ApiResponse<Void> deletePermission(@PathVariable String name) {
    permissionService.deletePermission(name);
    return ApiResponse.<Void>builder().message("Delete permission successfully").build();
  }
}
