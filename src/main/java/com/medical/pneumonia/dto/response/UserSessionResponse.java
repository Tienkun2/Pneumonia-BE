package com.medical.pneumonia.dto.response;

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
public class UserSessionResponse {
  String id;
  Instant loginTime;
  Instant expiryTime;
  String ipAddress;
  String userAgent;
  String status;
}
