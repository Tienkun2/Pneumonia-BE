package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.response.UserSessionResponse;
import com.medical.pneumonia.entity.UserSession;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSessionMapper {
  UserSessionResponse toUserSessionResponse(UserSession session);

  List<UserSessionResponse> toUserSessionResponseList(List<UserSession> sessions);
}
