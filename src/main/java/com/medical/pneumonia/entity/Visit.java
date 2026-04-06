package com.medical.pneumonia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
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
@Table(name = "visits")
public class Visit {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne
  @JoinColumn(name = "patient_id", nullable = false)
  Patient patient;

  @Column(nullable = false)
  Instant visitDate;

  @Column(columnDefinition = "TEXT")
  String symptoms;

  @Column(columnDefinition = "TEXT")
  String note;

  String createdBy;

  @OneToMany(
      mappedBy = "visit",
      cascade = jakarta.persistence.CascadeType.ALL,
      orphanRemoval = true)
  List<Diagnosis> diagnoses;

  @OneToMany(
      mappedBy = "visit",
      cascade = jakarta.persistence.CascadeType.ALL,
      orphanRemoval = true)
  List<MedicalImage> medicalImages;
}
