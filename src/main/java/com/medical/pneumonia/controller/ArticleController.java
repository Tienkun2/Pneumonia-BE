package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.ArticleCreationRequest;
import com.medical.pneumonia.dto.request.ArticleUpdateRequest;
import com.medical.pneumonia.dto.response.ArticleResponse;
import com.medical.pneumonia.service.ArticleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArticleController {
  ArticleService articleService;

  @GetMapping
  public ApiResponse<List<ArticleResponse>> getAllArticles(
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "category", required = false) String category) {
    List<ArticleResponse> result;
    if (search != null && !search.trim().isEmpty()) {
      result = articleService.searchArticles(search);
    } else if (category != null && !category.trim().isEmpty() && !category.equals("all")) {
      result = articleService.getArticlesByCategory(category);
    } else {
      result = articleService.getAllArticles();
    }
    return ApiResponse.<List<ArticleResponse>>builder()
        .message("Get articles list successfully")
        .result(result)
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<ArticleResponse> getArticleById(@PathVariable String id) {
    return ApiResponse.<ArticleResponse>builder()
        .message("Get article detail successfully")
        .result(articleService.getArticleById(id))
        .build();
  }

  @PostMapping
  public ApiResponse<ArticleResponse> createArticle(
      @RequestBody @Valid ArticleCreationRequest request) {
    return ApiResponse.<ArticleResponse>builder()
        .message("Create article successfully")
        .result(articleService.createArticle(request))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<ArticleResponse> updateArticle(
      @PathVariable String id, @RequestBody @Valid ArticleUpdateRequest request) {
    return ApiResponse.<ArticleResponse>builder()
        .message("Update article successfully")
        .result(articleService.updateArticle(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteArticle(@PathVariable String id) {
    articleService.deleteArticle(id);
    return ApiResponse.<Void>builder().message("Delete article successfully").build();
  }

  @PostMapping("/upload-image")
  public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
    return ApiResponse.<String>builder()
        .message("Upload article image successfully")
        .result(articleService.uploadArticleImage(file))
        .build();
  }
}
