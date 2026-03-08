package com.medical.pneumonia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.medical.pneumonia.entity.InvalidToken;

import jakarta.transaction.Transactional;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String>{
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM invalid_tokens WHERE expiry_time < NOW()", nativeQuery = true)
    void deleteExpiredTokens();
}
