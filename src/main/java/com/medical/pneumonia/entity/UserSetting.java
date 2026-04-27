package com.medical.pneumonia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "user_settings")
public class UserSetting {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true)
  User user;

  boolean darkMode;
  String language;
  boolean notifyDiagnosis;
  boolean notifySystem;
  boolean notifyPatient;
  boolean notifyPush;
  boolean notifySecurity;
}
