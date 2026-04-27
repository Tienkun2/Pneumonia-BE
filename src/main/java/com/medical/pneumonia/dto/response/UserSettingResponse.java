package com.medical.pneumonia.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSettingResponse {
  Boolean darkMode;
  String language;
  Boolean notifyDiagnosis;
  Boolean notifySystem;
  Boolean notifyPatient;
  Boolean notifyPush;
  Boolean notifySecurity;
}
