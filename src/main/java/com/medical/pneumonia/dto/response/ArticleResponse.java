package com.medical.pneumonia.dto.response;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArticleResponse {
  String id;
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
