package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.response.DashboardStatResponse;
import com.medical.pneumonia.dto.response.DiagnosisStatResponse;
import com.medical.pneumonia.dto.response.VisitResponse;
import com.medical.pneumonia.dto.response.VisitTrendResponse;
import com.medical.pneumonia.service.DashboardService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

  DashboardService dashboardService;

  @GetMapping("/summary")
  ApiResponse<DashboardStatResponse> getOverviewStatistics() {
    return ApiResponse.<DashboardStatResponse>builder()
        .message("Get overview statistics successfully")
        .result(dashboardService.getOverviewStatistics())
        .build();
  }

  @GetMapping("/visits-chart")
  ApiResponse<List<VisitTrendResponse>> getVisitTrends(
      @RequestParam(value = "range", required = false, defaultValue = "7d") String range) {
    return ApiResponse.<List<VisitTrendResponse>>builder()
        .message("Get visit trends successfully")
        .result(dashboardService.getVisitTrends(range))
        .build();
  }

  @GetMapping("/diagnosis-stats")
  ApiResponse<List<DiagnosisStatResponse>> getDiagnosisStats() {
    return ApiResponse.<List<DiagnosisStatResponse>>builder()
        .message("Get diagnosis statistics successfully")
        .result(dashboardService.getDiagnosisStats())
        .build();
  }

  @GetMapping("/recent-visits")
  ApiResponse<List<VisitResponse>> getRecentVisits(
      @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {
    return ApiResponse.<List<VisitResponse>>builder()
        .message("Get recent visits successfully")
        .result(dashboardService.getRecentVisits(limit))
        .build();
  }
}
