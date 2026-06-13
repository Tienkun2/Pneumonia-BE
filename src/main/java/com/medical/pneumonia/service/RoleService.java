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
import com.medical.pneumonia.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class RoleService {

  RoleRepository roleRepository;
  RoleMapper roleMapper;
  PermissionRepository permissionRepository;
  UserRepository userRepository;

  private Role getRoleEntity(String name) {
    return roleRepository
        .findByNameWithPermissions(name)
        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
  }

  @Transactional
  @CacheEvict(value = "menus", allEntries = true)
  public RoleResponse createRole(RoleCreationRequest request) {
    Role role = roleMapper.toRole(request);

    var permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));

    role = roleRepository.save(role);

    return roleMapper.toRoleResponse(role);
  }

  public List<RoleResponse> getAllRoles() {
    List<RoleResponse> roles = roleMapper.toListRoleResponse(roleRepository.findAll());
    Map<String, Long> userCounts =
        userRepository.countUsersByRole().stream()
            .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1], (a, b) -> a));
    roles.forEach(role -> role.setUserCount(userCounts.getOrDefault(role.getName(), 0L)));
    return roles;
  }

  public RoleResponse getRole(String name) {
    var role = getRoleEntity(name);
    var response = roleMapper.toRoleResponse(role);
    response.setUserCount(userRepository.countByRoleName(name));
    return response;
  }

  @Transactional
  @CacheEvict(value = "menus", allEntries = true)
  public void deleteRole(String name) {
    roleRepository.delete(getRoleEntity(name));
  }

  @Transactional
  @CacheEvict(value = "menus", allEntries = true)
  public RoleResponse updateRole(String name, RoleUpdateRequest request) {
    Role role = getRoleEntity(name);

    roleMapper.updateRole(role, request);

    var permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));

    roleRepository.save(role);

    return roleMapper.toRoleResponse(role);
  }
}
