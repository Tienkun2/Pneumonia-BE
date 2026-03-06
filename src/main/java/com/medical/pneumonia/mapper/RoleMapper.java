package com.medical.pneumonia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.medical.pneumonia.dto.request.RoleCreationRequest;
import com.medical.pneumonia.dto.request.RoleUpdateRequest;
import com.medical.pneumonia.dto.response.RoleResponse;
import com.medical.pneumonia.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
    
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    List<RoleResponse> toListRoleResponse(List<Role> role);

    @Mapping(target = "permissions", ignore = true)
    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);
}
