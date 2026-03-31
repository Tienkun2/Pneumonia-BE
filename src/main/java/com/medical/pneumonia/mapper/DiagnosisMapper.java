package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.response.DiagnosisResponse;
import com.medical.pneumonia.entity.Diagnosis;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {
  DiagnosisResponse toDiagnosisResponse(Diagnosis diagnosis);

  List<DiagnosisResponse> toDiagnosisResponse(List<Diagnosis> diagnoses);
}
