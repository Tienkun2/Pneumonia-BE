package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.RoleCreationRequest;
import com.medical.pneumonia.dto.request.RoleUpdateRequest;
import com.medical.pneumonia.dto.response.RoleResponse;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.RoleMapper;
import com.medical.pneumonia.repository.PermissionRepository;
import com.medical.pneumonia.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {

  RoleRepository roleRepository;
  RoleMapper roleMapper;
  PermissionRepository permissionRepository;

  private Role getRoleEntity(String name) {
    return roleRepository
        .findById(name)
        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
  }

  public RoleResponse createRole(RoleCreationRequest request) {
    Role role = roleMapper.toRole(request);

    var permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));

    role = roleRepository.save(role);

    return roleMapper.toRoleResponse(role);
  }

  public List<RoleResponse> getAllRoles() {
    return roleMapper.toListRoleResponse(roleRepository.findAll());
  }

  public RoleResponse getRole(String name) {
    var role = getRoleEntity(name);
    return roleMapper.toRoleResponse(role);
  }

  public void deleteRole(String name) {
    roleRepository.delete(getRoleEntity(name));
  }

  public RoleResponse updateRole(String name, RoleUpdateRequest request) {
    Role role = getRoleEntity(name);

    roleMapper.updateRole(role, request);

    var permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));

    roleRepository.save(role);

    return roleMapper.toRoleResponse(role);
  }
}
