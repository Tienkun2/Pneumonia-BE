package com.medical.pneumonia.service;

import com.medical.pneumonia.constant.DashboardConstants;
import com.medical.pneumonia.dto.response.DashboardStatResponse;
import com.medical.pneumonia.dto.response.DiagnosisStatResponse;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.dto.response.VisitTrendResponse;
import com.medical.pneumonia.enums.DashboardRange;
import com.medical.pneumonia.enums.DiagnosisResult;
import com.medical.pneumonia.repository.DiagnosisRepository;
import com.medical.pneumonia.repository.PatientRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.VisitRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {

  VisitRepository visitRepository;
  PatientRepository patientRepository;
  UserRepository userRepository;
  DiagnosisRepository diagnosisRepository;
  VisitService visitService;

  static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern(DashboardConstants.DATE_FORMAT).withZone(ZoneId.systemDefault());

  public DashboardStatResponse getOverviewStatistics() {
    return DashboardStatResponse.builder()
        .totalPatients(patientRepository.count())
        .totalVisits(visitRepository.count())
        .totalUsers(userRepository.count())
        .todayVisits(getTodayVisitsCount())
        .percentageIncrease(12.5)
        .build();
  }

  public List<VisitTrendResponse> getVisitTrends(String rangeKey) {
    try {
      DashboardRange range = DashboardRange.fromKey(rangeKey);
      LocalDate startDate = LocalDate.now().minusDays(range.getDays() - 1);
      Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

      Map<String, Long> visitsByDate =
          visitRepository.countVisitsByDateNative(startInstant).stream()
              .collect(
                  Collectors.toMap(row -> row[0].toString(), row -> ((Number) row[1]).longValue()));

      return Stream.iterate(startDate, d -> d.plusDays(1))
          .limit(range.getDays())
          .map(date -> buildTrendResponse(date, visitsByDate))
          .toList();
    } catch (Exception e) {
      log.error("Error generating visit trends", e);
      return Collections.emptyList();
    }
  }

  public List<DiagnosisStatResponse> getDiagnosisStats() {
    try {
      return diagnosisRepository.countDiagnosesByResult().stream()
          .map(this::mapToDiagnosisStat)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .toList();
    } catch (Exception e) {
      log.error("Error calculating diagnosis stats", e);
      return Collections.emptyList();
    }
  }

  public List<VisitResponse> getRecentVisits(int limit) {
    try {
      org.springframework.data.domain.Pageable pageable =
          org.springframework.data.domain.PageRequest.of(0, limit);
      var page = visitRepository.findByOrderByVisitDateDesc(pageable);

      List<String> visitIds =
          page.getContent().stream().map(com.medical.pneumonia.entity.Visit::getId).toList();
      return visitService.populateVisitResponses(page.getContent(), visitIds);
    } catch (Exception e) {
      log.error("Error fetching recent visits", e);
      return Collections.emptyList();
    }
  }

  private long getTodayVisitsCount() {
    Instant startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
    return visitRepository.countByVisitDateBetween(startOfToday, Instant.now());
  }

  private Optional<DiagnosisStatResponse> mapToDiagnosisStat(Object[] row) {
    if (row == null || row.length < 2 || row[0] == null) return Optional.empty();

    try {
      DiagnosisResult result;
      if (row[0] instanceof DiagnosisResult) {
        result = (DiagnosisResult) row[0];
      } else {
        result = DiagnosisResult.valueOf(row[0].toString().toUpperCase().trim());
      }

      long count = ((Number) row[1]).longValue();

      return Optional.of(
          DiagnosisStatResponse.builder()
              .label(result.name())
              .count(count)
              .color(getDiagnosisColor(result))
              .build());
    } catch (Exception e) {
      log.warn("Failed to map diagnosis stat row: {}", row[0], e);
      return Optional.empty();
    }
  }

  private VisitTrendResponse buildTrendResponse(LocalDate date, Map<String, Long> countMap) {
    String dateLabel = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    return VisitTrendResponse.builder()
        .date(dateLabel)
        .visits(countMap.getOrDefault(dateLabel, 0L))
        .build();
  }

  private String getDiagnosisColor(DiagnosisResult result) {
    return switch (result) {
      case NORMAL -> DashboardConstants.COLOR_NORMAL;
      case PNEUMONIA -> DashboardConstants.COLOR_PNEUMONIA;
      default -> DashboardConstants.COLOR_DEFAULT;
    };
  }
}
