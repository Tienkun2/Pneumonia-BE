package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.request.VisitCreationRequest;
import com.medical.pneumonia.dto.request.VisitUpdateRequest;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.entity.Visit;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VisitMapper {
  @Mapping(target = "patient", ignore = true)
  Visit toVisit(VisitCreationRequest request);

  @Mapping(source = "patient.id", target = "patientId")
  @Mapping(target = "medicalImages", ignore = true)
  @Mapping(target = "diagnoses", ignore = true)
  VisitResponse toVisitResponse(Visit visit);

  List<VisitResponse> toVisitResponse(List<Visit> visits);

  @Mapping(target = "patient", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateVisit(@MappingTarget Visit visit, VisitUpdateRequest request);
}
