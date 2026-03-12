package com.medical.pneumonia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.medical.pneumonia.dto.request.RoleCreationRequest;
import com.medical.pneumonia.dto.response.RoleResponse;
import com.medical.pneumonia.entity.Permission;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.mapper.RoleMapper;
import com.medical.pneumonia.repository.PermissionRepository;
import com.medical.pneumonia.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

  @Mock RoleRepository roleRepository;

  @Mock PermissionRepository permissionRepository;

  @Mock RoleMapper roleMapper;

  @InjectMocks RoleService roleService;

  Role role;
  RoleResponse response;
  Permission permission;

  @BeforeEach
  void setup() {

    permission = Permission.builder().name("READ_USER").build();

    role = Role.builder().name("ADMIN").build();

    response = RoleResponse.builder().name("ADMIN").build();
  }

  @Test
  void createRole_success() {

    RoleCreationRequest request = new RoleCreationRequest();
    request.setPermissions(Set.of("READ_USER"));

    when(roleMapper.toRole(request)).thenReturn(role);
    when(permissionRepository.findAllById(request.getPermissions()))
        .thenReturn(List.of(permission));
    when(roleRepository.save(role)).thenReturn(role);
    when(roleMapper.toRoleResponse(role)).thenReturn(response);

    RoleResponse result = roleService.createRole(request);

    assertNotNull(result);
    verify(roleRepository).save(role);
  }

  @Test
  void getAllRoles_success() {

    when(roleRepository.findAll()).thenReturn(List.of(role));
    when(roleMapper.toListRoleResponse(List.of(role))).thenReturn(List.of(response));

    List<RoleResponse> result = roleService.getAllRoles();

    assertEquals(1, result.size());
  }

  @Test
  void getRole_success() {

    when(roleRepository.findById("ADMIN")).thenReturn(Optional.of(role));

    when(roleMapper.toRoleResponse(role)).thenReturn(response);

    RoleResponse result = roleService.getRole("ADMIN");

    assertNotNull(result);
  }

  @Test
  void getRole_notFound() {

    when(roleRepository.findById("ADMIN")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> roleService.getRole("ADMIN"));
  }

  @Test
  void deleteRole_success() {

    when(roleRepository.findById("ADMIN")).thenReturn(Optional.of(role));

    roleService.deleteRole("ADMIN");

    verify(roleRepository).delete(role);
  }
}
