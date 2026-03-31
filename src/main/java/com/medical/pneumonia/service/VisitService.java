package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.MedicalImageCreationRequest;
import com.medical.pneumonia.dto.request.VisitCreationRequest;
import com.medical.pneumonia.dto.request.VisitUpdateRequest;
import com.medical.pneumonia.dto.response.DiagnosisResponse;
import com.medical.pneumonia.dto.response.MedicalImageResponse;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.entity.Diagnosis;
import com.medical.pneumonia.entity.MedicalImage;
import com.medical.pneumonia.entity.Patient;
import com.medical.pneumonia.entity.Visit;
import com.medical.pneumonia.enums.DiagnosisResult;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.DiagnosisMapper;
import com.medical.pneumonia.mapper.MedicalImageMapper;
import com.medical.pneumonia.mapper.VisitMapper;
import com.medical.pneumonia.repository.DiagnosisRepository;
import com.medical.pneumonia.repository.MedicalImageRepository;
import com.medical.pneumonia.repository.PatientRepository;
import com.medical.pneumonia.repository.VisitRepository;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VisitService {

  VisitRepository visitRepository;
  PatientRepository patientRepository;
  MedicalImageRepository medicalImageRepository;
  DiagnosisRepository diagnosisRepository;
  NotificationService notificationService;

  VisitMapper visitMapper;
  MedicalImageMapper medicalImageMapper;
  DiagnosisMapper diagnosisMapper;

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public VisitResponse createVisit(VisitCreationRequest request) {
    Patient patient =
        patientRepository
            .findById(request.getPatientId())
            .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

    Visit visit = visitMapper.toVisit(request);
    visit.setPatient(patient);
    visit.setVisitDate(Instant.now());

    var context = SecurityContextHolder.getContext();
    if (context != null && context.getAuthentication() != null) {
      visit.setCreatedBy(context.getAuthentication().getName());
    }

    visit = visitRepository.save(visit);
    return visitMapper.toVisitResponse(visit);
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public PageResponse<VisitResponse> getAllVisits(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = visitRepository.findAll(pageable);

    return PageResponse.<VisitResponse>builder()
        .currentPage(page)
        .pageSize(pageData.getSize())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(
            pageData.getContent().stream()
                .map(
                    visit -> {
                      VisitResponse response = visitMapper.toVisitResponse(visit);
                      response.setMedicalImages(
                          medicalImageMapper.toMedicalImageResponse(
                              medicalImageRepository.findByVisitId(visit.getId())));
                      response.setDiagnoses(
                          diagnosisMapper.toDiagnosisResponse(
                              diagnosisRepository.findByVisitId(visit.getId())));
                      return response;
                    })
                .toList())
        .build();
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public List<VisitResponse> getVisitsByPatientId(String patientId) {
    if (!patientRepository.existsById(patientId)) {
      throw new AppException(ErrorCode.PATIENT_NOT_FOUND);
    }
    List<Visit> visits = visitRepository.findByPatientId(patientId);
    return visits.stream()
        .map(
            visit -> {
              VisitResponse response = visitMapper.toVisitResponse(visit);
              response.setMedicalImages(
                  medicalImageMapper.toMedicalImageResponse(
                      medicalImageRepository.findByVisitId(visit.getId())));
              response.setDiagnoses(
                  diagnosisMapper.toDiagnosisResponse(
                      diagnosisRepository.findByVisitId(visit.getId())));
              return response;
            })
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public MedicalImageResponse uploadMedicalImage(
      String visitId, MedicalImageCreationRequest request) {
    Visit visit =
        visitRepository
            .findById(visitId)
            .orElseThrow(() -> new AppException(ErrorCode.VISIT_NOT_FOUND));

    MedicalImage image = medicalImageMapper.toMedicalImage(request);
    image.setVisit(visit);
    image.setUploadedAt(Instant.now());

    image = medicalImageRepository.save(image);
    return medicalImageMapper.toMedicalImageResponse(image);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR')")
  public DiagnosisResponse diagnose(String visitId) {
    Visit visit =
        visitRepository
            .findById(visitId)
            .orElseThrow(() -> new AppException(ErrorCode.VISIT_NOT_FOUND));

    Random random = new Random();
    DiagnosisResult randomResult =
        random.nextBoolean() ? DiagnosisResult.NORMAL : DiagnosisResult.PNEUMONIA;
    Double confidence = 0.5 + (random.nextDouble() * 0.49);

    Diagnosis diagnosis =
        Diagnosis.builder()
            .visit(visit)
            .result(randomResult)
            .confidenceScore(confidence)
            .modelVersion("v1.0")
            .doctorConfirm(false)
            .createdAt(Instant.now())
            .build();

    diagnosis = diagnosisRepository.save(diagnosis);

    // Push notification to the user who created the visit (or anyone subscribed)
    String doctorName = SecurityContextHolder.getContext().getAuthentication().getName();
    notificationService.sendToUser(
        doctorName,
        "/queue/notifications",
        "Lượt khám " + visitId + " đã có kết quả chẩn đoán: " + randomResult);

    return diagnosisMapper.toDiagnosisResponse(diagnosis);
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public VisitResponse updateVisit(String id, VisitUpdateRequest request) {
    Visit visit =
        visitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VISIT_NOT_FOUND));

    visitMapper.updateVisit(visit, request);
    return visitMapper.toVisitResponse(visitRepository.save(visit));
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public void deleteVisit(String id) {
    Visit visit =
        visitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VISIT_NOT_FOUND));
    visitRepository.delete(visit);
  }
}
