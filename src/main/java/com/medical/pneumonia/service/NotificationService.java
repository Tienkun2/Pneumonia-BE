package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.response.NotificationResponse;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.entity.Notification;
import com.medical.pneumonia.mapper.NotificationMapper;
import com.medical.pneumonia.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {
  SimpMessagingTemplate messagingTemplate;
  NotificationRepository notificationRepository;
  NotificationMapper notificationMapper;

  /** Gửi thông báo riêng cho 1 user (vừa lưu DB vừa push socket) */
  public void sendToUser(String username, String destination, String content) {
    Notification notification =
        Notification.builder()
            .recipientUsername(username)
            .content(content)
            .read(false)
            .createdAt(Instant.now())
            .build();
    notificationRepository.save(notification);

    NotificationResponse response = notificationMapper.toNotificationResponse(notification);
    messagingTemplate.convertAndSendToUser(username, destination, response);
    log.info("Sent notification to user [{}]: {}", username, content);
  }

  /** Gửi thông báo broadcast tới tất cả (topic), lưu DB với recipientUsername = "ALL" */
  public void sendToAll(String destination, String content) {
    Notification notification =
        Notification.builder()
            .recipientUsername("ALL")
            .content(content)
            .read(false)
            .createdAt(Instant.now())
            .build();
    notificationRepository.save(notification);

    NotificationResponse response = notificationMapper.toNotificationResponse(notification);
    messagingTemplate.convertAndSend(destination, response);
    log.info("Broadcast notification to [{}]: {}", destination, content);
  }

  /** Lấy danh sách thông báo: cả cá nhân (username) và broadcast (ALL) */
  public PageResponse<NotificationResponse> getMyNotifications(int page, int size) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Pageable pageable = PageRequest.of(page - 1, size);
    List<String> targets = List.of(username, "ALL");
    var pageData =
        notificationRepository.findByRecipientUsernameInOrderByCreatedAtDesc(targets, pageable);

    return PageResponse.<NotificationResponse>builder()
        .currentPage(page)
        .pageSize(pageData.getSize())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(
            pageData.getContent().stream().map(notificationMapper::toNotificationResponse).toList())
        .build();
  }

  /** Đếm số thông báo chưa đọc: cả cá nhân (username) và broadcast (ALL) */
  public long countUnread() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<String> targets = List.of(username, "ALL");
    return notificationRepository.countByRecipientUsernameInAndReadFalse(targets);
  }

  /** Đánh dấu tất cả thông báo là đã đọc */
  public void markAllAsRead() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<String> targets = List.of(username, "ALL");
    notificationRepository.markAllAsRead(targets);
    log.info("Marked all notifications as read for user [{}]", username);
  }

  /** Đánh dấu 1 thông báo cụ thể là đã đọc theo ID */
  public void markOneAsRead(String id) {
    notificationRepository.markOneAsRead(id);
  }

  /** Xóa 1 thông báo theo ID */
  public void deleteOne(String id) {
    notificationRepository.deleteOne(id);
  }

  /** Xóa tất cả thông báo của User hiện tại */
  public void deleteAll() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<String> targets = List.of(username, "ALL");
    notificationRepository.deleteAllByTargets(targets);
  }
}
