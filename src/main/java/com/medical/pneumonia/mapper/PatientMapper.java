package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.request.PatientCreationRequest;
import com.medical.pneumonia.dto.request.PatientUpdateRequest;
import com.medical.pneumonia.dto.response.PatientResponse;
import com.medical.pneumonia.entity.Patient;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PatientMapper {
  Patient toPatient(PatientCreationRequest request);

  PatientResponse toPatientResponse(Patient patient);

  List<PatientResponse> toPatientResponse(List<Patient> patients);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updatePatient(@MappingTarget Patient patient, PatientUpdateRequest request);
}
