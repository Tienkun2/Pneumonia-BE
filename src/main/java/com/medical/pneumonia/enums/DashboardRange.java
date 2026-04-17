package com.medical.pneumonia.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DashboardRange {
  SEVEN_DAYS("7d", 7),
  THIRTY_DAYS("30d", 30),
  TWELVE_MONTHS("12m", 365);

  private final String key;
  private final int days;

  public static DashboardRange fromKey(String key) {
    return Arrays.stream(values())
        .filter(r -> r.key.equalsIgnoreCase(key))
        .findFirst()
        .orElse(SEVEN_DAYS);
  }
}
