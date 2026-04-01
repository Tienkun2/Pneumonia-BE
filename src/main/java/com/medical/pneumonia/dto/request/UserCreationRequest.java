package com.medical.pneumonia.dto.request;

import jakarta.validation.constraints.Size;
import java.util.List;
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
public class UserCreationRequest {
  @Size(min = 6, message = "USERNAME_INVALID")
  String username;

  String email;

  String displayName;

  @com.medical.pneumonia.validator.DobConstraint(min = 18, message = "DOB_INVALID")
  java.time.LocalDate dob;

  List<String> roles;
}
