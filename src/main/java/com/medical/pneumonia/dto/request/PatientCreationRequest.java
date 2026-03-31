package com.medical.pneumonia.dto.request;

import com.medical.pneumonia.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientCreationRequest {
  @NotBlank(message = "Code must not be blank")
  String code;

  @NotBlank(message = "Full name must not be blank")
  String fullName;

  LocalDate dateOfBirth;

  Gender gender;

  String guardianName;

  String phone;

  String address;
}
