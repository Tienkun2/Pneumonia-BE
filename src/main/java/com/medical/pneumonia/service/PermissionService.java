package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.PermissionCreationRequest;
import com.medical.pneumonia.dto.request.PermissionUpdateRequest;
import com.medical.pneumonia.dto.response.PermissionResponse;
import com.medical.pneumonia.dto.response.PermissionTreeResponse;
import com.medical.pneumonia.entity.Permission;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.PermissionMapper;
import com.medical.pneumonia.repository.PermissionRepository;
import com.medical.pneumonia.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class PermissionService {
  PermissionRepository permissionRepository;
  PermissionMapper permissionMapper;
  RoleRepository roleRepository;

  private Permission getPermissionEntity(String name) {
    return permissionRepository
        .findById(name)
        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
  }

  @Transactional
  @CacheEvict(value = "menus", allEntries = true)
  public PermissionResponse createPermission(PermissionCreationRequest request) {
    Permission permission = permissionMapper.toPermission(request);
    permission = permissionRepository.save(permission);
    return permissionMapper.toPermissionResponse(permission);
  }

  public List<PermissionResponse> getRoots() {
    return permissionRepository.findAll().stream()
        .filter(p -> p.getParentName() == null || p.getParentName().isEmpty())
        .map(permissionMapper::toPermissionResponse)
        .collect(Collectors.toList());
  }

  public List<PermissionResponse> getChildren(String parentName) {
    return permissionRepository.findAll().stream()
        .filter(p -> parentName.equals(p.getParentName()))
        .map(permissionMapper::toPermissionResponse)
        .collect(Collectors.toList());
  }

  public List<PermissionResponse> getAllPermissions() {
    return permissionMapper.toListPermissionResponse(permissionRepository.findAll());
  }

  public PermissionResponse getPermission(String name) {
    return permissionMapper.toPermissionResponse(getPermissionEntity(name));
  }

  @Transactional
  @CacheEvict(value = "menus", allEntries = true)
  public void deletePermission(String name) {
    permissionRepository.delete(getPermissionEntity(name));
  }

  @Transactional
  @CacheEvict(value = "menus", allEntries = true)
  public PermissionResponse updatePermission(String name, PermissionUpdateRequest request) {
    var permission = getPermissionEntity(name);
    permissionMapper.updatePermission(permission, request);
    permissionRepository.save(permission);
    return permissionMapper.toPermissionResponse(permission);
  }

  public List<PermissionTreeResponse> getPermissionTree(String roleName) {
    Set<String> rolePermissions =
        roleRepository
            .findByNameWithPermissions(roleName)
            .map(
                role ->
                    role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet()))
            .orElse(new HashSet<>());

    List<Permission> all = permissionRepository.findAll();
    Map<String, List<Permission>> childrenByParent =
        all.stream()
            .filter(p -> p.getParentName() != null && !p.getParentName().isEmpty())
            .collect(Collectors.groupingBy(Permission::getParentName));

    return all.stream()
        .filter(p -> p.getParentName() == null || p.getParentName().isEmpty())
        .map(p -> buildTreeRecursive(p, childrenByParent, rolePermissions))
        .collect(Collectors.toList());
  }

  private PermissionTreeResponse buildTreeRecursive(
      Permission p, Map<String, List<Permission>> childrenMap, Set<String> checked) {

    List<Permission> children = childrenMap.getOrDefault(p.getName(), new java.util.ArrayList<>());

    List<PermissionTreeResponse> childrenResponse =
        children.stream()
            .map(child -> buildTreeRecursive(child, childrenMap, checked))
            .collect(Collectors.toList());

    return PermissionTreeResponse.builder()
        .name(p.getName())
        .description(p.getDescription())
        .level(p.getLevel() != null ? p.getLevel() : 0)
        .isChecked(checked.contains(p.getName()))
        .children(childrenResponse.isEmpty() ? null : childrenResponse)
        .build();
  }
}
