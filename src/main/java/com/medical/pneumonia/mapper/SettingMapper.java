package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.request.SystemSettingRequest;
import com.medical.pneumonia.dto.request.UserSettingRequest;
import com.medical.pneumonia.dto.response.SystemSettingResponse;
import com.medical.pneumonia.dto.response.UserSettingResponse;
import com.medical.pneumonia.entity.SystemSetting;
import com.medical.pneumonia.entity.UserSetting;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SettingMapper {
  SystemSettingResponse toSystemSettingResponse(SystemSetting systemSetting);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateSystemSetting(
      @MappingTarget SystemSetting systemSetting, SystemSettingRequest request);

  UserSettingResponse toUserSettingResponse(UserSetting userSetting);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateUserSetting(@MappingTarget UserSetting userSetting, UserSettingRequest request);
}
