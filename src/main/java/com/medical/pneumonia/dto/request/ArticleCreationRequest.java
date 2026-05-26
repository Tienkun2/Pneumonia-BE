package com.medical.pneumonia.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ArticleCreationRequest {
  @NotBlank(message = "Title must not be blank")
  String title;

  @NotBlank(message = "Category must not be blank")
  String category;

  @NotBlank(message = "Category label must not be blank")
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
