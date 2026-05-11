package com.medical.pneumonia.dto.response;

import com.medical.pneumonia.enums.ImageType;
import java.time.Instant;
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
public class MedicalImageResponse {
  String id;
  String imageUrl;
  ImageType type;
  Instant uploadedAt;
}
