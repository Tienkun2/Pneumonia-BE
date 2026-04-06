package com.medical.pneumonia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
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
    String contentType = file.getContentType();
    if (contentType == null
        || !(contentType.equals("image/jpeg")
            || contentType.equals("image/png")
            || contentType.equals("image/webp"))) {
      throw new AppException(ErrorCode.IMAGE_INVALID_TYPE);
    }

    try {
      return this.cloudinary
          .uploader()
          .upload(file.getBytes(), ObjectUtils.asMap("folder", "pneumonia-images"));
    } catch (IOException io) {
      throw new AppException(ErrorCode.UPLOAD_FAILED);
    }
  }

  public Map<?, ?> upload(String base64Content) {
    try {
      return this.cloudinary
          .uploader()
          .upload(base64Content, ObjectUtils.asMap("folder", "pneumonia-images"));
    } catch (IOException io) {
      throw new AppException(ErrorCode.UPLOAD_FAILED);
    }
  }

  public void delete(String publicId) {
    try {
      this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException io) {
      throw new AppException(ErrorCode.UPLOAD_FAILED);
    }
  }
}
