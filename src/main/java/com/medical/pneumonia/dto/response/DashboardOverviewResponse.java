package com.medical.pneumonia.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardOverviewResponse {
  DashboardStatResponse summary;
  List<VisitTrendResponse> trends;
  List<DiagnosisStatResponse> diagnosisStats;
  List<VisitResponse> recentVisits;
}
