package com.medical.pneumonia.dto.request;

import com.medical.pneumonia.enums.DiagnosisResult;
import com.medical.pneumonia.enums.ImageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveDiagnosisHistoryRequest {
  String patientId;
  String symptoms;
  String note;

  String imageUrl;
  ImageType imageType;

  DiagnosisResult result;
  Double confidenceScore;
  String modelVersion;
}
