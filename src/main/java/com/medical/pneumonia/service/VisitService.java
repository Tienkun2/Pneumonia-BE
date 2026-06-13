package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.MedicalImageCreationRequest;
import com.medical.pneumonia.dto.request.SaveDiagnosisHistoryRequest;
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
import com.medical.pneumonia.enums.ImageType;
import com.medical.pneumonia.enums.NotificationType;
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
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VisitService {

  VisitRepository visitRepository;
  PatientRepository patientRepository;
  MedicalImageRepository medicalImageRepository;
  DiagnosisRepository diagnosisRepository;
  NotificationService notificationService;
  CloudinaryService cloudinaryService;

  VisitMapper visitMapper;
  MedicalImageMapper medicalImageMapper;
  DiagnosisMapper diagnosisMapper;
  TransactionTemplate transactionTemplate;

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
    notificationService.sendToAll(
        NotificationType.PATIENT,
        "Một lượt khám mới vừa được tạo cho bệnh nhân: " + patient.getFullName());
    return visitMapper.toVisitResponse(visit);
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public PageResponse<VisitResponse> getAllVisits(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = visitRepository.findByOrderByVisitDateDesc(pageable);

    List<String> visitIds = pageData.getContent().stream().map(Visit::getId).toList();

    return PageResponse.<VisitResponse>builder()
        .currentPage(page)
        .pageSize(pageData.getSize())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(populateVisitResponses(pageData.getContent(), visitIds))
        .build();
  }

  public List<VisitResponse> populateVisitResponses(List<Visit> visits, List<String> visitIds) {
    var imagesMap =
        medicalImageRepository.findAllByVisitIdIn(visitIds).stream()
            .collect(Collectors.groupingBy(img -> img.getVisit().getId()));

    var diagnosesMap =
        diagnosisRepository.findAllByVisitIdIn(visitIds).stream()
            .collect(Collectors.groupingBy(diag -> diag.getVisit().getId()));

    return visits.stream()
        .map(
            visit -> {
              VisitResponse response = visitMapper.toVisitResponse(visit);
              var visitDiagnoses = diagnosesMap.getOrDefault(visit.getId(), List.of());

              response.setMedicalImages(
                  medicalImageMapper.toMedicalImageResponse(
                      imagesMap.getOrDefault(visit.getId(), List.of())));
              response.setDiagnoses(diagnosisMapper.toDiagnosisResponse(visitDiagnoses));

              if (!visitDiagnoses.isEmpty()) {
                response.setDiagnosisResult(visitDiagnoses.get(0).getResult().name());
              }

              return response;
            })
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public List<VisitResponse> getVisitsByPatientId(String patientId) {
    if (!patientRepository.existsById(patientId)) {
      throw new AppException(ErrorCode.PATIENT_NOT_FOUND);
    }
    List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patientId);
    List<String> visitIds = visits.stream().map(Visit::getId).toList();

    return populateVisitResponses(visits, visitIds);
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
        NotificationType.DIAGNOSIS,
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

  @PreAuthorize(
      "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_NURSE')")
  public VisitResponse saveDiagnosisHistory(SaveDiagnosisHistoryRequest request) {
    String finalImageUrl = request.getImageUrl();

    // Perform Cloudinary upload outside of @Transactional
    if (finalImageUrl != null && finalImageUrl.startsWith("data:image/")) {
      var uploadResult = cloudinaryService.upload(finalImageUrl);
      if (uploadResult != null && uploadResult.containsKey("secure_url")) {
        finalImageUrl = uploadResult.get("secure_url").toString();
      } else if (uploadResult != null && uploadResult.containsKey("url")) {
        finalImageUrl = uploadResult.get("url").toString();
      }
    }

    final String imageUrlToSave = finalImageUrl;

    return transactionTemplate.execute(
        status -> {
          Visit visit;
          if (request.getVisitId() != null && !request.getVisitId().trim().isEmpty()) {
            visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new AppException(ErrorCode.VISIT_NOT_FOUND));
            if (request.getSymptoms() != null) {
              visit.setSymptoms(request.getSymptoms());
            }
            if (request.getNote() != null) {
              visit.setNote(request.getNote());
            }
          } else {
            Patient patient =
                patientRepository
                    .findById(request.getPatientId())
                    .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

            visit =
                Visit.builder()
                    .patient(patient)
                    .symptoms(request.getSymptoms())
                    .note(request.getNote())
                    .visitDate(Instant.now())
                    .build();
          }

          var context = SecurityContextHolder.getContext();
          if (context != null && context.getAuthentication() != null) {
            visit.setCreatedBy(context.getAuthentication().getName());
          }

          Visit savedVisit = visitRepository.save(visit);

          MedicalImage image = null;
          if (imageUrlToSave != null && !imageUrlToSave.isEmpty()) {
            image =
                MedicalImage.builder()
                    .visit(savedVisit)
                    .imageUrl(imageUrlToSave)
                    .type(request.getImageType() != null ? request.getImageType() : ImageType.XRAY)
                    .uploadedAt(Instant.now())
                    .build();
            image = medicalImageRepository.save(image);
          }

          Diagnosis diagnosis = null;
          if (request.getResult() != null) {
            diagnosis =
                Diagnosis.builder()
                    .visit(savedVisit)
                    .result(request.getResult())
                    .confidenceScore(request.getConfidenceScore())
                    .modelVersion(request.getModelVersion())
                    .doctorConfirm(false)
                    .createdAt(Instant.now())
                    .build();
            diagnosis = diagnosisRepository.save(diagnosis);
          }

          VisitResponse response = visitMapper.toVisitResponse(savedVisit);
          if (image != null) {
            response.setMedicalImages(List.of(medicalImageMapper.toMedicalImageResponse(image)));
          } else {
            response.setMedicalImages(List.of());
          }

          if (diagnosis != null) {
            response.setDiagnoses(List.of(diagnosisMapper.toDiagnosisResponse(diagnosis)));
          } else {
            response.setDiagnoses(List.of());
          }

          return response;
        });
  }
}
