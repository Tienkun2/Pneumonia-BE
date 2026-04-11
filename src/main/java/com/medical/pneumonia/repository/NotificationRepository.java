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

  long countByRecipientUsernameInAndReadFalse(List<String> recipientUsernames);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Notification n SET n.read = true WHERE n.recipientUsername IN :usernames AND n.read = false")
  void markAllAsRead(@Param("usernames") List<String> usernames);

  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id")
  void markOneAsRead(@Param("id") String id);

  @Modifying
  @Transactional
  @Query("DELETE FROM Notification n WHERE n.id = :id")
  void deleteOne(@Param("id") String id);

  @Modifying
  @Transactional
  @Query("DELETE FROM Notification n WHERE n.recipientUsername IN :usernames")
  void deleteAllByTargets(@Param("usernames") List<String> usernames);
}
