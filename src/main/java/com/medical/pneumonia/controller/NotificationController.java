package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.response.NotificationResponse;
import com.medical.pneumonia.dto.response.PageResponse;
import com.medical.pneumonia.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

  NotificationService notificationService;

  @GetMapping
  ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
    return ApiResponse.<PageResponse<NotificationResponse>>builder()
        .message("Notifications fetched successfully")
        .result(notificationService.getMyNotifications(page, size))
        .build();
  }

  @GetMapping("/unread-count")
  ApiResponse<Long> countUnread() {
    return ApiResponse.<Long>builder()
        .message("Unread count fetched successfully")
        .result(notificationService.countUnread())
        .build();
  }

  @PutMapping("/mark-all-read")
  ApiResponse<Void> markAllAsRead() {
    notificationService.markAllAsRead();
    return ApiResponse.<Void>builder().message("All notifications marked as read").build();
  }

  @PutMapping("/{id}/read")
  ApiResponse<Void> markOneAsRead(@PathVariable String id) {
    notificationService.markOneAsRead(id);
    return ApiResponse.<Void>builder().message("Notification marked as read").build();
  }

  @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
  ApiResponse<Void> deleteOne(@PathVariable String id) {
    notificationService.deleteOne(id);
    return ApiResponse.<Void>builder().message("Notification deleted successfully").build();
  }

  @org.springframework.web.bind.annotation.DeleteMapping
  ApiResponse<Void> deleteAll() {
    notificationService.deleteAll();
    return ApiResponse.<Void>builder().message("All notifications deleted successfully").build();
  }
}
