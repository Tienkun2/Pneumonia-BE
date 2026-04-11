package com.medical.pneumonia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "notifications", indexes = {
    @jakarta.persistence.Index(name = "idx_noti_recipient_created", columnList = "recipientUsername, createdAt"),
    @jakarta.persistence.Index(name = "idx_noti_is_read", columnList = "is_read")
})
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(nullable = false)
  String recipientUsername;

  @Column(nullable = false, columnDefinition = "TEXT")
  String content;

  @Column(name = "is_read", nullable = false)
  @Builder.Default
  boolean read = false;

  @Column(nullable = false)
  Instant createdAt;
}
