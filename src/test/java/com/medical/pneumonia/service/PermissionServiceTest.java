package com.medical.pneumonia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medical.pneumonia.dto.request.PermissionCreationRequest;
import com.medical.pneumonia.dto.request.PermissionUpdateRequest;
import com.medical.pneumonia.dto.response.PermissionResponse;
import com.medical.pneumonia.entity.Permission;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.mapper.PermissionMapper;
import com.medical.pneumonia.repository.PermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    PermissionRepository permissionRepository;

    @Mock
    PermissionMapper permissionMapper;

    @InjectMocks
    PermissionService permissionService;

    Permission permission;
    PermissionResponse response;

    @BeforeEach
    void setup() {
        permission = Permission.builder()
                .name("READ_USER")
                .build();

        response = PermissionResponse.builder()
                .name("READ_USER")
                .build();
    }

    @Test
    void createPermission_success() {

        PermissionCreationRequest request = new PermissionCreationRequest();

        when(permissionMapper.toPermission(request)).thenReturn(permission);
        when(permissionRepository.save(permission)).thenReturn(permission);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(response);

        PermissionResponse result = permissionService.createPermission(request);

        assertNotNull(result);
        verify(permissionRepository).save(permission);
    }

    @Test
    void getAllPermissions_success() {

        when(permissionRepository.findAll()).thenReturn(List.of(permission));
        when(permissionMapper.toListPermissionResponse(List.of(permission)))
                .thenReturn(List.of(response));

        List<PermissionResponse> result = permissionService.getAllPermissions();

        assertEquals(1, result.size());
    }

    @Test
    void getPermission_success() {

        when(permissionRepository.findById("READ_USER"))
                .thenReturn(Optional.of(permission));

        when(permissionMapper.toPermissionResponse(permission))
                .thenReturn(response);

        PermissionResponse result = permissionService.getPermission("READ_USER");

        assertNotNull(result);
    }

    @Test
    void getPermission_notFound_throwException() {

        when(permissionRepository.findById("READ_USER"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> permissionService.getPermission("READ_USER"));
    }

    @Test
    void deletePermission_success() {

        when(permissionRepository.findById("READ_USER"))
                .thenReturn(Optional.of(permission));

        permissionService.deletePermission("READ_USER");

        verify(permissionRepository).delete(permission);
    }

    @Test
    void updatePermission_success() {

        PermissionUpdateRequest request = new PermissionUpdateRequest();

        when(permissionRepository.findById("READ_USER"))
                .thenReturn(Optional.of(permission));

        when(permissionRepository.save(permission))
                .thenReturn(permission);

        when(permissionMapper.toPermissionResponse(permission))
                .thenReturn(response);

        PermissionResponse result =
                permissionService.updatePermission("READ_USER", request);

        assertNotNull(result);

        verify(permissionMapper).updatePermission(permission, request);
    }
}