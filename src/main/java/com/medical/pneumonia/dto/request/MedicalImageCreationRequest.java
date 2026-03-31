package com.medical.pneumonia.dto.request;

import com.medical.pneumonia.enums.ImageType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalImageCreationRequest {
  String imageUrl;
  ImageType type;
}
