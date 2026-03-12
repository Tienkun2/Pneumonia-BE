package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.RoleCreationRequest;
import com.medical.pneumonia.dto.request.RoleUpdateRequest;
import com.medical.pneumonia.dto.response.RoleResponse;
import com.medical.pneumonia.service.RoleService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/roles")
public class RoleController {
  RoleService roleService;

  @PostMapping()
  ApiResponse<RoleResponse> createRole(@RequestBody RoleCreationRequest request) {
    return ApiResponse.<RoleResponse>builder()
        .message("Create role successfully")
        .result(roleService.createRole(request))
        .build();
  }

  @GetMapping()
  ApiResponse<List<RoleResponse>> getListRole() {
    return ApiResponse.<List<RoleResponse>>builder()
        .message("Get list role successfully")
        .result(roleService.getAllRoles())
        .build();
  }

  @GetMapping("/{name}")
  ApiResponse<RoleResponse> getRole(@PathVariable String name) {
    return ApiResponse.<RoleResponse>builder()
        .message("Get role successfully")
        .result(roleService.getRole(name))
        .build();
  }

  @DeleteMapping("/{name}")
  ApiResponse<Void> deleteRole(@PathVariable String name) {
    roleService.deleteRole(name);
    return ApiResponse.<Void>builder().message("Delete role successfully").build();
  }

  @PutMapping("/{name}")
  ApiResponse<RoleResponse> updateRole(
      @PathVariable String name, @RequestBody RoleUpdateRequest request) {
    return ApiResponse.<RoleResponse>builder()
        .message("Update role successfully")
        .result(roleService.updateRole(name, request))
        .build();
  }
}
