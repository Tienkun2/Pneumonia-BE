package com.medical.pneumonia.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionTreeResponse {
  String name;
  String description;
  int level;
  boolean isChecked;
  List<PermissionTreeResponse> children;
}
