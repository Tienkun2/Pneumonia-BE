package com.medical.pneumonia.dto.request;

import com.medical.pneumonia.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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

  @Size(min = 6, message = "PASSWORD_INVALID")
  String password;

  @DobConstraint(min = 2, message = "DOB_INVALID")
  LocalDate dob;

  List<String> roles;
}
