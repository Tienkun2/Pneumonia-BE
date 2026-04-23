package com.medical.pneumonia.service;

import com.medical.pneumonia.constant.UserStatus;
import com.medical.pneumonia.dto.request.ChangePasswordRequest;
import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.request.UserUpdateRequest;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.enums.SessionStatus;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.UserMapper;
import com.medical.pneumonia.repository.RoleRepository;
import com.medical.pneumonia.repository.UserDeviceRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.UserSessionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

  UserRepository userRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;
  RoleRepository roleRepository;
  EmailService emailService;
  CloudinaryService cloudinaryService;
  NotificationService notificationService;
  UserDeviceRepository userDeviceRepository;
  UserSessionRepository userSessionRepository;

  public UserResponse uploadAvatar(MultipartFile file) {
    var context = SecurityContextHolder.getContext();
    String username = context.getAuthentication().getName();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    var result = cloudinaryService.upload(file);
    user.setAvatar(result.get("url").toString());
    return toUserResponseWithDeviceCount(userRepository.save(user));
  }

  private User getUserEntity(String id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public UserResponse createUser(UserCreationRequest request) {

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    User user = userMapper.toUser(request);

    if (request.getDob() != null && request.getDob().plusYears(18).isAfter(LocalDate.now())) {
      throw new AppException(ErrorCode.DOB_INVALID);
    }

    Role role =
        roleRepository
            .findById("USER")
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

    user.setRoles(Set.of(role));

    String token = UUID.randomUUID().toString();
    user.setActivationToken(token);
    user.setActivationTokenExpiry(Instant.now().plus(1, ChronoUnit.DAYS));
    user.setStatus(UserStatus.PENDING);
    user.setCreatedAt(Instant.now());

    try {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    emailService.sendActivationEmail(request.getEmail(), user.getUsername(), token);

    notificationService.sendToAll(
        "/topic/admin/notifications", "Một tài khoản mới vừa được khởi tạo: " + user.getUsername());

    return toUserResponseWithDeviceCount(user);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public PageResponse<UserResponse> getAllUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userRepository.findAll(pageable);

    return PageResponse.<UserResponse>builder()
        .currentPage(page)
        .pageSize(pageData.getSize())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(pageData.getContent().stream().map(this::toUserResponseWithDeviceCount).toList())
        .build();
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal.getClaim('sub')")
  public UserResponse getUserById(String id) {
    return toUserResponseWithDeviceCount(getUserEntity(id));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void deleteUser(String id) {
    User user = getUserEntity(id);
    userRepository.delete(user);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public UserResponse updateUser(String id, UserUpdateRequest request) {

    User user = getUserEntity(id);

    if (request.getUsername() != null
        && !request.getUsername().equals(user.getUsername())
        && userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    if (request.getEmail() != null
        && !request.getEmail().equals(user.getEmail())
        && userRepository.existsByEmail(request.getEmail())) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    userMapper.updateUser(user, request);

    if (request.getRoles() != null) {
      var roles = roleRepository.findAllById(request.getRoles());
      user.setRoles(new HashSet<>(roles));
    }

    user = userRepository.save(user);

    return toUserResponseWithDeviceCount(user);
  }

  public UserResponse getMyInfo() {

    var context = SecurityContextHolder.getContext();
    String username = context.getAuthentication().getName();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return toUserResponseWithDeviceCount(user);
  }

  private UserResponse toUserResponseWithDeviceCount(User user) {
    UserResponse response = userMapper.toUserResponse(user);
    response.setDeviceCount(userDeviceRepository.countByUser(user));
    response.setSessionCount(
        userSessionRepository.countByUserAndStatus(user, SessionStatus.ACTIVE));
    return response;
  }

  public void setPassword(String token, String password) {
    User user =
        userRepository
            .findByActivationToken(token)
            .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTIVATION_TOKEN));

    if (user.getActivationTokenExpiry().isBefore(Instant.now())) {
      throw new AppException(ErrorCode.INVALID_ACTIVATION_TOKEN);
    }

    user.setPassword(passwordEncoder.encode(password));
    user.setStatus(UserStatus.ACTIVE);
    user.setActivationToken(null);
    user.setActivationTokenExpiry(null);
    userRepository.save(user);
    userRepository.save(user);
  }

  public void resendActivation(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    if (UserStatus.ACTIVE.equals(user.getStatus())) {
      throw new AppException(ErrorCode.USER_ALREADY_ACTIVE);
    }

    String token = UUID.randomUUID().toString();
    user.setActivationToken(token);
    user.setActivationTokenExpiry(Instant.now().plus(1, ChronoUnit.DAYS));
    userRepository.save(user);

    emailService.sendActivationEmail(email, user.getUsername(), token);
  }

  public void changePassword(ChangePasswordRequest request) {
    var context = SecurityContextHolder.getContext();
    String username = context.getAuthentication().getName();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    if (request.getOldPassword() != null
        && !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }
}
