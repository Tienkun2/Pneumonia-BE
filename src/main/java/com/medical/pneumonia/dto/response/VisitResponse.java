package com.medical.pneumonia.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
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
