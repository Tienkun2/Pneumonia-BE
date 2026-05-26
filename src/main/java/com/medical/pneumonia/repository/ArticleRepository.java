package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
  List<Article> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String title, String description);

  List<Article> findByCategory(String category);
}
