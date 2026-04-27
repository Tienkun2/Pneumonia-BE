package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.SystemSettingRequest;
import com.medical.pneumonia.dto.request.UserSettingRequest;
import com.medical.pneumonia.dto.response.SystemSettingResponse;
import com.medical.pneumonia.dto.response.UserSettingResponse;
import com.medical.pneumonia.entity.SystemSetting;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserSetting;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.SettingMapper;
import com.medical.pneumonia.repository.SystemSettingRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.UserSettingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingService {

  SystemSettingRepository systemSettingRepository;
  UserSettingRepository userSettingRepository;
  UserRepository userRepository;
  SettingMapper settingMapper;

  private static final String DEFAULT_SYSTEM_ID = "DEFAULT";

  public SystemSettingResponse getSystemSettings() {
    SystemSetting systemSetting =
        systemSettingRepository
            .findById(DEFAULT_SYSTEM_ID)
            .orElseGet(
                () ->
                    systemSettingRepository.save(
                        SystemSetting.builder()
                            .id(DEFAULT_SYSTEM_ID)
                            .hospitalName("Bệnh Viện Phổi Trung Ương")
                            .systemId("VH-LUNG-001")
                            .build()));
    return settingMapper.toSystemSettingResponse(systemSetting);
  }

  @Transactional
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public SystemSettingResponse updateSystemSettings(SystemSettingRequest request) {
    SystemSetting systemSetting =
        systemSettingRepository
            .findById(DEFAULT_SYSTEM_ID)
            .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

    settingMapper.updateSystemSetting(systemSetting, request);
    return settingMapper.toSystemSettingResponse(systemSettingRepository.save(systemSetting));
  }

  public UserSettingResponse getUserSettings() {
    User user = getCurrentUser();
    UserSetting userSetting =
        userSettingRepository
            .findByUser(user)
            .orElseGet(
                () ->
                    userSettingRepository.save(
                        UserSetting.builder()
                            .user(user)
                            .darkMode(false)
                            .language("vi")
                            .notifyDiagnosis(true)
                            .notifySystem(true)
                            .notifyPatient(false)
                            .notifyPush(false)
                            .notifySecurity(true)
                            .build()));
    return settingMapper.toUserSettingResponse(userSetting);
  }

  @Transactional
  public UserSettingResponse updateUserSettings(UserSettingRequest request) {
    User user = getCurrentUser();
    UserSetting userSetting =
        userSettingRepository
            .findByUser(user)
            .orElseGet(() -> UserSetting.builder().user(user).build());

    settingMapper.updateUserSetting(userSetting, request);
    return settingMapper.toUserSettingResponse(userSettingRepository.save(userSetting));
  }

  private User getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }
}
