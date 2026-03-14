package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.request.UserUpdateRequest;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.UserMapper;
import com.medical.pneumonia.repository.RoleRepository;
import com.medical.pneumonia.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

  UserRepository userRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;
  RoleRepository roleRepository;

  private User getUserEntity(String id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public UserResponse createUser(UserCreationRequest request) {

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    User user = userMapper.toUser(request);

    user.setPassword(passwordEncoder.encode(request.getPassword()));

    Role role =
        roleRepository
            .findById("USER")
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

    user.setRoles(Set.of(role));

    try {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    return userMapper.toUserResponse(user);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
  }

  public UserResponse getUserById(String id) {
    return userMapper.toUserResponse(getUserEntity(id));
  }

  public void deleteUser(String id) {
    userRepository.delete(getUserEntity(id));
  }

  public UserResponse updateUser(String id, UserUpdateRequest request) {

    User user = getUserEntity(id);

    userMapper.updateUser(user, request);

    user.setPassword(passwordEncoder.encode(request.getPassword()));

    var roles = roleRepository.findAllById(request.getRoles());
    user.setRoles(new HashSet<>(roles));

    return userMapper.toUserResponse(userRepository.save(user));
  }

  public UserResponse getMyInfo() {

    var context = SecurityContextHolder.getContext();
    String username = context.getAuthentication().getName();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toUserResponse(user);
  }
}
