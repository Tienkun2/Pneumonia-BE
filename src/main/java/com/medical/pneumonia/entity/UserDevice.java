package com.medical.pneumonia.entity;

import com.medical.pneumonia.enums.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "user_devices")
public class UserDevice {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Enumerated(EnumType.STRING)
  DeviceType deviceType;

  String appName;

  String status;

  Instant lastAccess;

  Instant firstAccess;

  String ipAddress;

  @Column(columnDefinition = "TEXT")
  String userAgent;

  @Column(columnDefinition = "boolean default false")
  boolean isRemembered = false;
}
