package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.response.NotificationResponse;
import com.medical.pneumonia.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
  NotificationResponse toNotificationResponse(Notification notification);
}
