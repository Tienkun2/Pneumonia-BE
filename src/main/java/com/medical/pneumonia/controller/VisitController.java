package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.MedicalImageCreationRequest;
import com.medical.pneumonia.dto.request.VisitCreationRequest;
import com.medical.pneumonia.dto.request.VisitUpdateRequest;
import com.medical.pneumonia.dto.response.DiagnosisResponse;
import com.medical.pneumonia.dto.response.MedicalImageResponse;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.service.VisitService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VisitController {

  VisitService visitService;

  @PostMapping()
  ApiResponse<VisitResponse> createVisit(@RequestBody @Valid VisitCreationRequest request) {
    return ApiResponse.<VisitResponse>builder()
        .message("Visit created successfully")
        .result(visitService.createVisit(request))
        .build();
  }

  @GetMapping()
  ApiResponse<PageResponse<VisitResponse>> getAllVisits(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<VisitResponse>>builder()
        .message("Get visit list successfully")
        .result(visitService.getAllVisits(page, size))
        .build();
  }

  @PostMapping("/{id}/images")
  ApiResponse<MedicalImageResponse> uploadMedicalImage(
      @PathVariable String id, @RequestBody @Valid MedicalImageCreationRequest request) {
    return ApiResponse.<MedicalImageResponse>builder()
        .message("Medical image uploaded successfully")
        .result(visitService.uploadMedicalImage(id, request))
        .build();
  }

  @PostMapping("/{id}/diagnose")
  ApiResponse<DiagnosisResponse> diagnose(@PathVariable String id) {
    return ApiResponse.<DiagnosisResponse>builder()
        .message("Diagnosis completed successfully")
        .result(visitService.diagnose(id))
        .build();
  }

  @PutMapping("/{id}")
  ApiResponse<VisitResponse> updateVisit(
      @PathVariable String id, @RequestBody @Valid VisitUpdateRequest request) {
    return ApiResponse.<VisitResponse>builder()
        .message("Visit updated successfully")
        .result(visitService.updateVisit(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  ApiResponse<Void> deleteVisit(@PathVariable String id) {
    visitService.deleteVisit(id);
    return ApiResponse.<Void>builder().message("Visit deleted successfully").build();
  }
}
