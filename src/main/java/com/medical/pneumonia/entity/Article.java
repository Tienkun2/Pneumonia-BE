package com.medical.pneumonia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "articles")
public class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(nullable = false)
  String title;

  @Column(nullable = false)
  String category;

  String categoryLabel;

  @Column(columnDefinition = "TEXT")
  String description;

  @Column(columnDefinition = "TEXT")
  String content;

  String author;
  String readTime;
  String date;
  String image;
  String tags;
  boolean isFeatured;
}
