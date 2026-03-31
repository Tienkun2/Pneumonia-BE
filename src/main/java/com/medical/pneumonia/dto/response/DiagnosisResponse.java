package com.medical.pneumonia.dto.response;

import com.medical.pneumonia.enums.DiagnosisResult;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiagnosisResponse {
  String id;
  DiagnosisResult result;
  Double confidenceScore;
  String modelVersion;
  Boolean doctorConfirm;
  String note;
  Instant createdAt;
}
