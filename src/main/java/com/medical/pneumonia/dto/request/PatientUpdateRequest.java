package com.medical.pneumonia.dto.request;

import com.medical.pneumonia.enums.Gender;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientUpdateRequest {
  String fullName;
  LocalDate dateOfBirth;
  Gender gender;
  String guardianName;
  String phone;
  String address;
}
