package com.medical.pneumonia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

  Cloudinary cloudinary;

  public Map<?, ?> upload(MultipartFile file) {
    try {
      return this.cloudinary
          .uploader()
          .upload(file.getBytes(), ObjectUtils.asMap("folder", "pneumonia-images"));
    } catch (IOException io) {
      throw new RuntimeException("Image upload failed", io);
    }
  }

  public void delete(String publicId) {
    try {
      this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException io) {
      throw new RuntimeException("Image delete failed", io);
    }
  }
}
