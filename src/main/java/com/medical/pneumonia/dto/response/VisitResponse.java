package com.medical.pneumonia.dto.response;

import java.time.Instant;
import java.util.List;
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
public class VisitResponse {
  String id;
  String patientId;
  String patientName;
  Instant visitDate;
  String symptoms;
  String note;
  String createdBy;
  String diagnosisResult;
  List<MedicalImageResponse> medicalImages;
  List<DiagnosisResponse> diagnoses;
}
