package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserSession;
import com.medical.pneumonia.enums.SessionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
  List<UserSession> findByUserIdOrderByLoginTimeDesc(String userId);

  Page<UserSession> findByUserIdOrderByLoginTimeDesc(String userId, Pageable pageable);

  Optional<UserSession> findByTokenId(String tokenId);

  List<UserSession> findByDeviceIdAndStatus(String deviceId, SessionStatus status);

  long countByUserAndStatus(User user, SessionStatus status);
}
