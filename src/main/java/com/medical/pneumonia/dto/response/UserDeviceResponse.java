package com.medical.pneumonia.dto.response;

import com.medical.pneumonia.enums.DeviceType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDeviceResponse {
  String id;
  DeviceType deviceType;
  String appName;
  String status;
  Instant lastAccess;
  Instant firstAccess;
  String ipAddress;
  boolean remembered;
  boolean current;
}
