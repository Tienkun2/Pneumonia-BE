package com.medical.pneumonia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.medical.pneumonia.constant.UserStatus;
import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.request.UserUpdateRequest;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.mapper.UserMapper;
import com.medical.pneumonia.repository.RoleRepository;
import com.medical.pneumonia.repository.UserDeviceRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.UserSessionRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock UserRepository userRepository;

  @Mock RoleRepository roleRepository;

  @Mock PasswordEncoder passwordEncoder;

  @Mock UserMapper userMapper;

  @Mock UserDeviceRepository userDeviceRepository;
  @Mock UserSessionRepository userSessionRepository;
  @Mock EmailService emailService;
  @Mock CloudinaryService cloudinaryService;
  @Mock NotificationService notificationService;

  @InjectMocks UserService userService;

  User user;
  UserResponse response;
  Role userRole;
  UserCreationRequest creationRequest;
  UserUpdateRequest updateRequest;

  @BeforeEach
  void setUp() {

    userRole = Role.builder().name("DEFAULT").build();

    user =
        User.builder()
            .id("1")
            .username("testuser")
            .activationToken("valid-token")
            .activationTokenExpiry(Instant.now().plusSeconds(3600))
            .roles(Set.of(userRole))
            .build();

    response = UserResponse.builder().username("testuser").build();

    creationRequest =
        UserCreationRequest.builder().username("testuser").email("test@example.com").build();

    updateRequest = UserUpdateRequest.builder().roles(List.of("DEFAULT")).build();
  }

  // ================= CREATE USER =================

  @Test
  void createUser_success() {

    when(userRepository.existsByUsername("testuser")).thenReturn(false);

    when(userMapper.toUser(creationRequest)).thenReturn(user);

    when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

    when(roleRepository.findById("USER")).thenReturn(Optional.of(userRole));

    when(userRepository.save(user)).thenReturn(user);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    UserResponse result = userService.createUser(creationRequest);

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());

    verify(userRepository).save(user);
  }

  @Test
  void createUser_userAlreadyExists_throwException() {

    when(userRepository.existsByUsername("testuser")).thenReturn(true);

    assertThrows(AppException.class, () -> userService.createUser(creationRequest));

    verify(userRepository, never()).save(any());
  }

  @Test
  void createUser_roleNotFound_throwException() {

    when(userRepository.existsByUsername("testuser")).thenReturn(false);

    when(userMapper.toUser(creationRequest)).thenReturn(user);

    when(roleRepository.findById("USER")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userService.createUser(creationRequest));
  }

  @Test
  void createUser_passwordShouldBeEncoded() {

    when(userRepository.existsByUsername("testuser")).thenReturn(false);

    when(userMapper.toUser(creationRequest)).thenReturn(user);

    when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

    when(roleRepository.findById("USER")).thenReturn(Optional.of(userRole));

    when(userRepository.save(user)).thenReturn(user);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    userService.createUser(creationRequest);

    verify(passwordEncoder).encode("123456");
  }

  @Test
  void createUser_shouldAssignUserRole() {

    when(userRepository.existsByUsername("testuser")).thenReturn(false);

    when(userMapper.toUser(creationRequest)).thenReturn(user);

    when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

    when(roleRepository.findById("USER")).thenReturn(Optional.of(userRole));

    when(userRepository.save(user)).thenReturn(user);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    userService.createUser(creationRequest);

    assertTrue(user.getRoles().contains(userRole));
  }

  // ================= GET USER =================

  @Test
  void getUserById_success() {

    when(userRepository.findById("1")).thenReturn(Optional.of(user));

    when(userMapper.toUserResponse(user)).thenReturn(response);

    UserResponse result = userService.getUserById("1");

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
  }

  @Test
  void getUserById_userNotFound_throwException() {

    when(userRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userService.getUserById("1"));
  }

  // ================= DELETE USER =================

  @Test
  void deleteUser_success() {

    when(userRepository.findById("1")).thenReturn(Optional.of(user));

    userService.deleteUser("1");

    verify(userRepository).delete(user);
  }

  @Test
  void deleteUser_userNotFound_throwException() {

    when(userRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userService.deleteUser("1"));
  }

  // ================= UPDATE USER =================

  @Test
  void updateUser_success() {

    when(userRepository.findById("1")).thenReturn(Optional.of(user));

    when(roleRepository.findAllById(updateRequest.getRoles())).thenReturn(List.of(userRole));

    when(userRepository.save(user)).thenReturn(user);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    UserResponse result = userService.updateUser("1", updateRequest);

    assertNotNull(result);

    verify(userMapper).updateUser(user, updateRequest);
    verify(userRepository).save(user);
  }

  @Test
  void updateUser_rolesShouldBeUpdated() {

    when(userRepository.findById("1")).thenReturn(Optional.of(user));

    when(roleRepository.findAllById(updateRequest.getRoles())).thenReturn(List.of(userRole));

    when(userRepository.save(user)).thenReturn(user);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    userService.updateUser("1", updateRequest);

    assertEquals(1, user.getRoles().size());
  }

  // ================= GET ALL USERS =================

  @Test
  void getAllUsers_success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(user));

    when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    PageResponse<UserResponse> result = userService.getAllUsers(1, 10);

    assertEquals(1, result.getData().size());
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getAllUsers_mapperShouldBeCalled() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(user));

    when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

    when(userMapper.toUserResponse(user)).thenReturn(response);

    userService.getAllUsers(1, 10);

    verify(userMapper).toUserResponse(user);
  }

  // ================= GET MY INFO =================

  @Test
  void getMyInfo_success() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(userMapper.toUserResponse(user)).thenReturn(response);

    UserResponse result = userService.getMyInfo("testuser");

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
  }

  @Test
  void getMyInfo_userNotFound_throwException() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userService.getMyInfo("testuser"));
  }

  // ================= VERIFY ACTIVATION TOKEN =================

  @Test
  void verifyActivationToken_success() {

    when(userRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(user));

    verify(userRepository).findByActivationToken("valid-token");
  }

  @Test
  void verifyActivationToken_invalidToken_throwException() {

    when(userRepository.findByActivationToken("invalid-token")).thenReturn(Optional.empty());
  }

  @Test
  void verifyActivationToken_expiredToken_throwException() {

    user.setActivationTokenExpiry(Instant.now().minusSeconds(3600));

    when(userRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(user));
  }

  // ================= SET PASSWORD =================

  @Test
  void setPassword_success() {

    when(userRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(user));

    when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");

    when(userRepository.save(user)).thenReturn(user);

    userService.setPassword("valid-token", "newpassword");

    assertEquals(UserStatus.ACTIVE, user.getStatus());
    assertNull(user.getActivationToken());
    assertNull(user.getActivationTokenExpiry());
    verify(passwordEncoder).encode("newpassword");
    verify(userRepository).save(user);
  }

  @Test
  void setPassword_invalidToken_throwException() {

    when(userRepository.findByActivationToken("invalid-token")).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userService.setPassword("invalid-token", "newpassword"));
  }

  @Test
  void setPassword_expiredToken_throwException() {

    user.setActivationTokenExpiry(java.time.Instant.now().minusSeconds(3600));

    when(userRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(user));

    assertThrows(AppException.class, () -> userService.setPassword("valid-token", "newpassword"));
  }
}
