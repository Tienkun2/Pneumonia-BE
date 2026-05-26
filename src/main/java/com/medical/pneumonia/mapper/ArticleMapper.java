package com.medical.pneumonia.mapper;

import com.medical.pneumonia.dto.request.ArticleCreationRequest;
import com.medical.pneumonia.dto.request.ArticleUpdateRequest;
import com.medical.pneumonia.dto.response.ArticleResponse;
import com.medical.pneumonia.entity.Article;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
  Article toArticle(ArticleCreationRequest request);

  ArticleResponse toArticleResponse(Article article);

  List<ArticleResponse> toArticleResponseList(List<Article> articles);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateArticle(@MappingTarget Article article, ArticleUpdateRequest request);
}
