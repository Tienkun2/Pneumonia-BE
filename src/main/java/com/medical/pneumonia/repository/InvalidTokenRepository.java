package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.InvalidToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM invalid_tokens WHERE expiry_time < NOW()", nativeQuery = true)
  void deleteExpiredTokens();
}
