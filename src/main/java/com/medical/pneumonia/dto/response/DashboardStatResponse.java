package com.medical.pneumonia.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardStatResponse {
  long totalPatients;
  long totalVisits;
  long totalUsers;
  long todayVisits;
  double percentageIncrease;
}
