package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
  List<UserSession> findByUserIdOrderByLoginTimeDesc(String userId);

  org.springframework.data.domain.Page<UserSession> findByUserIdOrderByLoginTimeDesc(
      String userId, org.springframework.data.domain.Pageable pageable);

  Optional<UserSession> findByTokenId(String tokenId);

  List<UserSession> findByDeviceIdAndStatus(String deviceId, String status);

  long countByUserAndStatus(User user, String status);
}
