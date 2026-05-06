package com.medical.pneumonia.entity;

import com.medical.pneumonia.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
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
@Table(
    name = "patients",
    indexes = {
      @Index(name = "idx_patient_code", columnList = "code"),
      @Index(name = "idx_patient_full_name", columnList = "fullName"),
      @Index(name = "idx_patient_created_at", columnList = "createdAt")
    })
public class Patient {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(unique = true, nullable = false)
  String code;

  @Column(nullable = false)
  String fullName;

  LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  Gender gender;

  String guardianName;

  String phone;

  String address;

  Instant createdAt;

  @OneToMany(
      mappedBy = "patient",
      cascade = jakarta.persistence.CascadeType.ALL,
      orphanRemoval = true)
  List<Visit> visits;
}
