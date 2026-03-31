package com.medical.pneumonia.dto.response;

import com.medical.pneumonia.enums.Gender;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientResponse {
  String id;
  String code;
  String fullName;
  LocalDate dateOfBirth;
  Gender gender;
  String guardianName;
  String phone;
  String address;
  Instant createdAt;
}
