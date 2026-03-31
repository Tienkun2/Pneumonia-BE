package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

  Page<Notification> findByRecipientUsernameInOrderByCreatedAtDesc(
      List<String> recipientUsernames, Pageable pageable);

  long countByRecipientUsernameInAndIsReadFalse(List<String> recipientUsernames);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Notification n SET n.isRead = true WHERE n.recipientUsername IN :usernames AND n.isRead = false")
  void markAllAsRead(@Param("usernames") List<String> usernames);

  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
  void markOneAsRead(@Param("id") String id);
}
