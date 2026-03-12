package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.request.PermissionCreationRequest;
import com.medical.pneumonia.dto.request.PermissionUpdateRequest;
import com.medical.pneumonia.dto.response.PermissionResponse;
import com.medical.pneumonia.entity.Permission;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
  PermissionResponse toPermissionResponse(Permission permission);

  Permission toPermission(PermissionCreationRequest request);

  List<PermissionResponse> toListPermissionResponse(List<Permission> permissions);

  void updatePermission(@MappingTarget Permission permission, PermissionUpdateRequest request);
}
