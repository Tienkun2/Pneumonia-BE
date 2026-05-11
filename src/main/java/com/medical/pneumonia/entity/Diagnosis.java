package com.medical.pneumonia.entity;

import com.medical.pneumonia.enums.DiagnosisResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name = "diagnoses",
    indexes = {
      @Index(name = "idx_diagnosis_visit_id", columnList = "visit_id"),
      @Index(name = "idx_diagnosis_result", columnList = "result")
    })
public class Diagnosis {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
  @JoinColumn(name = "visit_id", nullable = false)
  Visit visit;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  DiagnosisResult result;

  Double confidenceScore;

  String modelVersion;

  Boolean doctorConfirm;

  @Column(columnDefinition = "TEXT")
  String note;

  Instant createdAt;
}
