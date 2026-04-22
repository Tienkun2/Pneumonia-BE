package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.response.UserDeviceResponse;
import com.medical.pneumonia.entity.UserDevice;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDeviceMapper {
  @org.mapstruct.Mapping(source = "remembered", target = "remembered")
  UserDeviceResponse toUserDeviceResponse(UserDevice userDevice);

  List<UserDeviceResponse> toUserDeviceResponseList(List<UserDevice> userDevices);
}
