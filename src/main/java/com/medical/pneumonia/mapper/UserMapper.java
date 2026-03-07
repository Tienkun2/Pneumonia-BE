package com.medical.pneumonia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.request.UserUpdateRequest;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponse(List<User> user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}