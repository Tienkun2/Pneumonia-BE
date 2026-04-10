package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.PermissionCreationRequest;
import com.medical.pneumonia.dto.request.PermissionUpdateRequest;
import com.medical.pneumonia.dto.response.PermissionResponse;
import com.medical.pneumonia.dto.response.PermissionTreeResponse;
import com.medical.pneumonia.service.PermissionService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/roots")
  ApiResponse<List<PermissionResponse>> getRoots() {
    return ApiResponse.<List<PermissionResponse>>builder()
        .result(permissionService.getRoots())
        .build();
  }

  @GetMapping("/children/{parentName}")
  ApiResponse<List<PermissionResponse>> getChildren(@PathVariable String parentName) {
    return ApiResponse.<List<PermissionResponse>>builder()
        .result(permissionService.getChildren(parentName))
        .build();
  }

  @GetMapping("/info/{name}")
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

  @PutMapping("/info/{name}")
  ApiResponse<PermissionResponse> updatePermission(
      @PathVariable String name, @RequestBody PermissionUpdateRequest request) {
    return ApiResponse.<PermissionResponse>builder()
        .message("Update permission successfully")
        .result(permissionService.updatePermission(name, request))
        .build();
  }

  @GetMapping("/tree")
  ApiResponse<List<PermissionTreeResponse>> getPermissionTree(@RequestParam String roleName) {
    return ApiResponse.<List<PermissionTreeResponse>>builder()
        .message("Get permission tree successfully")
        .result(permissionService.getPermissionTree(roleName))
        .build();
  }
}
