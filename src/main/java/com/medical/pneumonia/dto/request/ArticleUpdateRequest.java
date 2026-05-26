package com.medical.pneumonia.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArticleUpdateRequest {
  String title;
  String category;
  String categoryLabel;
  String description;
  String content;
  String author;
  String readTime;
  String date;
  String image;
  String tags;
  boolean isFeatured;
}
