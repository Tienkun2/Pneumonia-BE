package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.PermissionCreationRequest;
import com.medical.pneumonia.dto.request.PermissionUpdateRequest;
import com.medical.pneumonia.dto.response.PermissionResponse;
import com.medical.pneumonia.entity.Permission;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.PermissionMapper;
import com.medical.pneumonia.repository.PermissionRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
  PermissionRepository permissionRepository;
  PermissionMapper permissionMapper;

  private Permission getPermissionEntity(String name) {
    return permissionRepository
        .findById(name)
        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
  }

  public PermissionResponse createPermission(PermissionCreationRequest request) {
    Permission permission = permissionMapper.toPermission(request);
    permission = permissionRepository.save(permission);
    return permissionMapper.toPermissionResponse(permission);
  }

  public List<PermissionResponse> getAllPermissions() {
    return permissionMapper.toListPermissionResponse(permissionRepository.findAll());
  }

  public PermissionResponse getPermission(String name) {
    return permissionMapper.toPermissionResponse(getPermissionEntity(name));
  }

  public void deletePermission(String name) {
    permissionRepository.delete(getPermissionEntity(name));
  }

  public PermissionResponse updatePermission(String name, PermissionUpdateRequest request) {
    var permission = getPermissionEntity(name);

    permissionMapper.updatePermission(permission, request);

    permissionRepository.save(permission);

    return permissionMapper.toPermissionResponse(permission);
  }
}
