package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.request.MedicalImageCreationRequest;
import com.medical.pneumonia.dto.response.MedicalImageResponse;
import com.medical.pneumonia.entity.MedicalImage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicalImageMapper {
  @Mapping(target = "visit", ignore = true)
  MedicalImage toMedicalImage(MedicalImageCreationRequest request);

  MedicalImageResponse toMedicalImageResponse(MedicalImage medicalImage);

  List<MedicalImageResponse> toMedicalImageResponse(List<MedicalImage> medicalImages);
}
