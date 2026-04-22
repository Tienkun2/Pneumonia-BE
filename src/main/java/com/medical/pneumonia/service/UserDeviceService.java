package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.response.UserDeviceResponse;
import com.medical.pneumonia.entity.InvalidToken;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserDevice;
import com.medical.pneumonia.entity.UserSession;
import com.medical.pneumonia.enums.DeviceType;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.mapper.UserDeviceMapper;
import com.medical.pneumonia.repository.InvalidTokenRepository;
import com.medical.pneumonia.repository.UserDeviceRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.repository.UserSessionRepository;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDeviceService {

  UserDeviceRepository userDeviceRepository;
  UserRepository userRepository;
  UserDeviceMapper userDeviceMapper;
  UserSessionRepository userSessionRepository;
  InvalidTokenRepository invalidTokenRepository;

  public List<UserDeviceResponse> getUserDevices(String userId, String currentDeviceId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    List<UserDeviceResponse> responses =
        userDeviceMapper.toUserDeviceResponseList(
            userDeviceRepository.findByUserOrderByLastAccessDesc(user));

    if (currentDeviceId != null) {
      responses.forEach(
          r -> {
            if (currentDeviceId.equals(r.getId())) {
              r.setCurrent(true);
            }
          });
    }
    return responses;
  }

  public List<UserDeviceResponse> getMyDevices(String currentUserId, String currentDeviceId) {
    return getUserDevices(currentUserId, currentDeviceId);
  }

  public UserDevice recordDeviceAccess(
      User user, String userAgent, String ipAddress, boolean rememberMe) {
    try {
      if (userAgent == null) userAgent = "Unknown";

      final String finalUa = userAgent;
      DeviceType type = parseDeviceType(finalUa);
      String app = parseAppName(finalUa);

      UserDevice device =
          userDeviceRepository
              .findByUserIdAndUserAgent(user.getId(), finalUa)
              .orElseGet(
                  () -> {
                    List<UserDevice> existingDevices =
                        userDeviceRepository.findByUserOrderByLastAccessDesc(user);
                    return existingDevices.stream()
                        .filter(d -> d.getDeviceType() == type && d.getAppName().equals(app))
                        .findFirst()
                        .map(
                            d -> {
                              d.setUserAgent(finalUa);
                              return d;
                            })
                        .orElseGet(
                            () ->
                                UserDevice.builder()
                                    .user(user)
                                    .userAgent(finalUa)
                                    .deviceType(type)
                                    .appName(app)
                                    .firstAccess(Instant.now())
                                    .status("Đang hoạt động")
                                    .build());
                  });

      device.setIpAddress(ipAddress);
      device.setLastAccess(Instant.now());
      if (rememberMe) {
        device.setRemembered(true);
      }
      return userDeviceRepository.save(device);
    } catch (Exception e) {
      log.error("Failed to record user device access", e);
      return null;
    }
  }

  public void revokeDevice(String deviceId) {
    UserDevice device =
        userDeviceRepository
            .findById(deviceId)
            .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    device.setRemembered(false);
    device.setStatus("Bị thu hồi");
    userDeviceRepository.save(device);

    // Revoke all active sessions for this device
    List<UserSession> activeSessions =
        userSessionRepository.findByDeviceIdAndStatus(deviceId, "ACTIVE");

    activeSessions.forEach(
        session -> {
          session.setStatus("REVOKED");
          userSessionRepository.save(session);

          // Blacklist the token
          InvalidToken invalidToken =
              InvalidToken.builder()
                  .id(session.getTokenId())
                  .expiryTime(Date.from(session.getExpiryTime()))
                  .build();
          invalidTokenRepository.save(invalidToken);
        });
  }

  public void deleteDevice(String deviceId) {
    userDeviceRepository.deleteById(deviceId);
  }

  public void trustDevice(String deviceId) {
    UserDevice device =
        userDeviceRepository
            .findById(deviceId)
            .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    device.setRemembered(true);
    userDeviceRepository.save(device);
  }

  private DeviceType parseDeviceType(String userAgent) {
    if (userAgent == null) return DeviceType.UNKNOWN;
    String ua = userAgent.toLowerCase();
    if (ua.contains("mobi") || ua.contains("android") || ua.contains("iphone")) {
      return DeviceType.MOBILE;
    }
    if (ua.contains("tablet") || ua.contains("ipad")) {
      return DeviceType.TABLET;
    }
    return DeviceType.PC;
  }

  private String parseAppName(String userAgent) {
    if (userAgent == null || userAgent.isEmpty() || userAgent.equals("Unknown"))
      return "Unknown Browser";

    String os = "Unknown OS";
    if (userAgent.contains("Windows NT 10.0")) os = "Windows 10/11";
    else if (userAgent.contains("Windows NT 6.1")) os = "Windows 7";
    else if (userAgent.contains("Macintosh")) os = "macOS";
    else if (userAgent.contains("X11")) os = "Linux";
    else if (userAgent.contains("Android")) os = "Android";
    else if (userAgent.contains("iPhone")) os = "iOS";

    String browser = "Unknown Browser";
    if (userAgent.contains("Edg/")) browser = "Edge";
    else if (userAgent.contains("Chrome/")) browser = "Chrome";
    else if (userAgent.contains("Firefox/")) browser = "Firefox";
    else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) browser = "Safari";
    else if (userAgent.contains("Postman")) browser = "Postman";

    return os + " / " + browser;
  }
}
