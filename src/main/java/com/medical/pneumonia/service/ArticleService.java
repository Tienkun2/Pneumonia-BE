package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.ArticleCreationRequest;
import com.medical.pneumonia.dto.request.ArticleUpdateRequest;
import com.medical.pneumonia.dto.response.ArticleResponse;
import com.medical.pneumonia.entity.Article;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.ArticleMapper;
import com.medical.pneumonia.repository.ArticleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArticleService {
  ArticleRepository articleRepository;
  ArticleMapper articleMapper;
  CloudinaryService cloudinaryService;

  public List<ArticleResponse> getAllArticles() {
    return articleRepository.findAll().stream()
        .map(this::toArticleResponse)
        .collect(Collectors.toList());
  }

  public List<ArticleResponse> getArticlesByCategory(String category) {
    return articleRepository.findByCategory(category).stream()
        .map(this::toArticleResponse)
        .collect(Collectors.toList());
  }

  public List<ArticleResponse> searchArticles(String query) {
    return articleRepository
        .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
        .stream()
        .map(this::toArticleResponse)
        .collect(Collectors.toList());
  }

  public ArticleResponse getArticleById(String id) {
    Article article =
        articleRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.ARTICLE_NOT_FOUND));
    return toArticleResponse(article);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR')")
  public ArticleResponse createArticle(ArticleCreationRequest request) {
    Article article = articleMapper.toArticle(request);
    return articleMapper.toArticleResponse(articleRepository.save(article));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR')")
  public ArticleResponse updateArticle(String id, ArticleUpdateRequest request) {
    Article article =
        articleRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.ARTICLE_NOT_FOUND));
    articleMapper.updateArticle(article, request);
    return articleMapper.toArticleResponse(articleRepository.save(article));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR')")
  public void deleteArticle(String id) {
    Article article =
        articleRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.ARTICLE_NOT_FOUND));
    articleRepository.delete(article);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DOCTOR')")
  public String uploadArticleImage(MultipartFile file) {
    var result = cloudinaryService.upload(file);
    return result.get("url").toString();
  }

  private ArticleResponse toArticleResponse(Article article) {
    return ArticleResponse.builder()
        .id(article.getId())
        .title(article.getTitle())
        .category(article.getCategory())
        .categoryLabel(article.getCategoryLabel())
        .description(article.getDescription())
        .content(article.getContent())
        .author(article.getAuthor())
        .readTime(article.getReadTime())
        .date(article.getDate())
        .image(article.getImage())
        .tags(article.getTags())
        .isFeatured(article.isFeatured())
        .build();
  }
}
