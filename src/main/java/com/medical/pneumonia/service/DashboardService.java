package com.medical.pneumonia.service;

import com.medical.pneumonia.constant.DashboardConstants;
import com.medical.pneumonia.dto.response.DashboardStatResponse;
import com.medical.pneumonia.dto.response.DiagnosisStatResponse;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.dto.response.VisitTrendResponse;
import com.medical.pneumonia.entity.Visit;
import com.medical.pneumonia.enums.DashboardRange;
import com.medical.pneumonia.enums.DiagnosisResult;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.repository.DiagnosisRepository;
import com.medical.pneumonia.repository.PatientRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.VisitRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  @Cacheable(value = "dashboard_stats", key = "'overview'")
  public DashboardStatResponse getOverviewStatistics() {
    var totalPatientsFuture = CompletableFuture.supplyAsync(patientRepository::count);
    var totalVisitsFuture = CompletableFuture.supplyAsync(visitRepository::count);
    var totalUsersFuture = CompletableFuture.supplyAsync(userRepository::count);
    var todayVisitsFuture = CompletableFuture.supplyAsync(this::getTodayVisitsCount);

    try {
      return DashboardStatResponse.builder()
          .totalPatients(totalPatientsFuture.get())
          .totalVisits(totalVisitsFuture.get())
          .totalUsers(totalUsersFuture.get())
          .todayVisits(todayVisitsFuture.get())
          .percentageIncrease(12.5) // This could also be calculated dynamically
          .build();
    } catch (Exception e) {
      log.error("Error gathering overview statistics", e);
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
  }

  @Cacheable(value = "dashboard_stats", key = "#rangeKey")
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
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
  }

  @Cacheable(value = "dashboard_stats", key = "'diagnosis'")
  public List<DiagnosisStatResponse> getDiagnosisStats() {
    try {
      return diagnosisRepository.countDiagnosesByResult().stream()
          .map(this::mapToDiagnosisStat)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .toList();
    } catch (Exception e) {
      log.error("Error calculating diagnosis stats", e);
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
  }

  public List<VisitResponse> getRecentVisits(int limit) {
    try {
      Pageable pageable = PageRequest.of(0, limit);
      var page = visitRepository.findByOrderByVisitDateDesc(pageable);

      List<String> visitIds = page.getContent().stream().map(Visit::getId).toList();
      return visitService.populateVisitResponses(page.getContent(), visitIds);
    } catch (Exception e) {
      log.error("Error fetching recent visits", e);
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
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
