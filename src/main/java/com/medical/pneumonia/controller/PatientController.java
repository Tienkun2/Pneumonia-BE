package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.PatientCreationRequest;
import com.medical.pneumonia.dto.request.PatientUpdateRequest;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.PatientResponse;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.service.PatientService;
import com.medical.pneumonia.service.VisitService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/patients")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatientController {

  PatientService patientService;
  VisitService visitService;

  @PostMapping()
  ApiResponse<PatientResponse> createPatient(@RequestBody @Valid PatientCreationRequest request) {
    return ApiResponse.<PatientResponse>builder()
        .message("Patient created successfully")
        .result(patientService.createPatient(request))
        .build();
  }

  @GetMapping()
  ApiResponse<PageResponse<PatientResponse>> getAllPatients(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<PatientResponse>>builder()
        .message("Get patient list successfully")
        .result(patientService.getAllPatients(page, size))
        .build();
  }

  @GetMapping("/{id}")
  ApiResponse<PatientResponse> getPatientById(@PathVariable String id) {
    return ApiResponse.<PatientResponse>builder()
        .message("Patient found successfully")
        .result(patientService.getPatientById(id))
        .build();
  }

  @GetMapping("/{id}/visits")
  ApiResponse<List<VisitResponse>> getVisitsByPatientId(@PathVariable String id) {
    return ApiResponse.<List<VisitResponse>>builder()
        .message("Get patient visits successfully")
        .result(visitService.getVisitsByPatientId(id))
        .build();
  }

  @PutMapping("/{id}")
  ApiResponse<PatientResponse> updatePatient(
      @PathVariable String id, @RequestBody @Valid PatientUpdateRequest request) {
    return ApiResponse.<PatientResponse>builder()
        .message("Patient updated successfully")
        .result(patientService.updatePatient(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  ApiResponse<Void> deletePatient(@PathVariable String id) {
    patientService.deletePatient(id);
    return ApiResponse.<Void>builder().message("Patient deleted successfully").build();
  }
}
