package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.response.NotificationResponse;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.entity.Notification;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserSetting;
import com.medical.pneumonia.enums.NotificationType;
import com.medical.pneumonia.mapper.NotificationMapper;
import com.medical.pneumonia.repository.NotificationRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.UserSettingRepository;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
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
  UserSettingRepository userSettingRepository;
  UserRepository userRepository;

  /** Gửi thông báo riêng cho 1 user (vừa lưu DB vừa push socket) - Có kiểm tra cài đặt */
  @Async
  public void sendToUser(String username, NotificationType type, String content) {
    // 1. Kiểm tra cài đặt nhận thông báo của User
    if (!isNotificationEnabled(username, type)) {
      log.info("Notification skipped for user [{}] because [{}] is disabled", username, type);
      return;
    }

    // 2. Lưu và gửi nếu được phép
    Notification notification =
        Notification.builder()
            .recipientUsername(username)
            .content(content)
            .type(type)
            .read(false)
            .createdAt(Instant.now())
            .build();
    notificationRepository.save(notification);

    NotificationResponse response = notificationMapper.toNotificationResponse(notification);
    messagingTemplate.convertAndSendToUser(username, "/queue/notifications", response);
    log.info("Sent [{}] notification to user [{}]: {}", type, username, content);
  }

  /** Kiểm tra xem user có bật loại thông báo này không */
  @Cacheable(value = "user_settings", key = "#username + '_' + #type")
  public boolean isNotificationEnabled(String username, NotificationType type) {
    User user = userRepository.findByUsername(username).orElse(null);
    if (user == null) return false;

    UserSetting settings = userSettingRepository.findByUser(user).orElse(null);
    if (settings == null) return true; // Mặc định bật nếu chưa có setting

    return switch (type) {
      case DIAGNOSIS -> settings.isNotifyDiagnosis();
      case SYSTEM -> settings.isNotifySystem();
      case PATIENT -> settings.isNotifyPatient();
      case SECURITY -> settings.isNotifySecurity();
      default -> true;
    };
  }

  /** Gửi thông báo broadcast tới tất cả (topic) */
  @Async
  public void sendToAll(NotificationType type, String content) {
    Notification notification =
        Notification.builder()
            .recipientUsername("ALL")
            .content(content)
            .type(type)
            .read(false)
            .createdAt(Instant.now())
            .build();
    notificationRepository.save(notification);

    NotificationResponse response = notificationMapper.toNotificationResponse(notification);
    messagingTemplate.convertAndSend("/topic/notifications", response);
    log.info("Broadcast [{}] notification: {}", type, content);
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

  /** Đếm số thông báo chưa đọc */
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

  public void markOneAsRead(String id) {
    notificationRepository.markOneAsRead(id);
  }

  public void deleteOne(String id) {
    notificationRepository.deleteOne(id);
  }

  public void deleteAll() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<String> targets = List.of(username, "ALL");
    notificationRepository.deleteAllByTargets(targets);
  }
}
