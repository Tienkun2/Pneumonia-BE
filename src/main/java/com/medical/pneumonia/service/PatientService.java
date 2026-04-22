package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.PatientCreationRequest;
import com.medical.pneumonia.dto.request.PatientUpdateRequest;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.PatientResponse;
import com.medical.pneumonia.entity.Patient;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.PatientMapper;
import com.medical.pneumonia.repository.PatientRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatientService {

  PatientRepository patientRepository;
  PatientMapper patientMapper;

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public PatientResponse createPatient(PatientCreationRequest request) {
    if (patientRepository.existsByCode(request.getCode())) {
      throw new AppException(ErrorCode.PATIENT_EXISTED);
    }

    Patient patient = patientMapper.toPatient(request);
    patient.setCreatedAt(Instant.now());

    patient = patientRepository.save(patient);
    return patientMapper.toPatientResponse(patient);
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public PageResponse<PatientResponse> getAllPatients(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = patientRepository.findAll(pageable);

    return PageResponse.<PatientResponse>builder()
        .currentPage(page)
        .pageSize(pageData.getSize())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(pageData.getContent().stream().map(patientMapper::toPatientResponse).toList())
        .build();
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public PatientResponse getPatientById(String id) {
    Patient patient =
        patientRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));
    return patientMapper.toPatientResponse(patient);
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public PatientResponse updatePatient(String id, PatientUpdateRequest request) {
    Patient patient =
        patientRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

    patientMapper.updatePatient(patient, request);
    patient = patientRepository.save(patient);
    return patientMapper.toPatientResponse(patient);
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public void deletePatient(String id) {
    Patient patient =
        patientRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));
    patientRepository.delete(patient);
  }
}
