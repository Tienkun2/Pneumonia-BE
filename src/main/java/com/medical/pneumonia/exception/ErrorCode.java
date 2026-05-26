package com.medical.pneumonia.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_KEY(9998, "Khóa thông báo không hợp lệ", HttpStatus.BAD_REQUEST),
  // Authentication
  UNAUTHENTICATED(1001, "Phiên đăng nhập đã hết hạn hoặc không hợp lệ", HttpStatus.UNAUTHORIZED),
  LOGIN_FAILED(1003, "Tên đăng nhập hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED(1002, "Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
  // Permission
  PERMISSION_NOT_FOUND(2001, "Không tìm thấy quyền hạn", HttpStatus.NOT_FOUND),
  // Role
  ROLE_NOT_FOUND(3001, "Không tìm thấy vai trò", HttpStatus.NOT_FOUND),
  // User
  USER_EXISTED(4001, "Tên đăng nhập đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND(4002, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
  USER_NOT_EXISTED(4003, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
  USERNAME_INVALID(4004, "Tên đăng nhập phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
  PASSWORD_INVALID(4005, "Mật khẩu phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
  DOB_INVALID(
      4006, "Ngày sinh không hợp lệ hoặc không đủ tuổi theo yêu cầu", HttpStatus.BAD_REQUEST),
  INVALID_ACTIVATION_TOKEN(4007, "Mã kích hoạt không hợp lệ", HttpStatus.BAD_REQUEST),
  ACTIVATION_TOKEN_EXPIRED(4008, "Mã kích hoạt đã hết hạn", HttpStatus.BAD_REQUEST),
  USER_ALREADY_ACTIVE(4009, "Người dùng đã được kích hoạt trước đó", HttpStatus.BAD_REQUEST),
  OLD_PASSWORD_INCORRECT(4010, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),
  USER_NOT_ACTIVE(4011, "Tài khoản chưa được kích hoạt hoặc đã bị khóa", HttpStatus.BAD_REQUEST),

  // Patient
  PATIENT_EXISTED(5001, "Mã bệnh nhân đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),
  PATIENT_NOT_FOUND(5002, "Không tìm thấy thông tin bệnh nhân", HttpStatus.NOT_FOUND),

  // Visit
  VISIT_NOT_FOUND(6001, "Không tìm thấy thông tin lượt khám", HttpStatus.NOT_FOUND),

  // File Upload
  IMAGE_INVALID_TYPE(7001, "Ảnh phải thuộc định dạng JPEG, PNG hoặc WEBP", HttpStatus.BAD_REQUEST),
  UPLOAD_FAILED(
      7002, "Quá trình tải ảnh lên máy chủ Cloudinary gặp lỗi", HttpStatus.INTERNAL_SERVER_ERROR),

  // Article
  ARTICLE_NOT_FOUND(8001, "Không tìm thấy bài viết", HttpStatus.NOT_FOUND),
  ;

  ErrorCode(int code, String message, HttpStatus httpStatusCode) {
    this.code = code;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
  }

  private int code;
  private String message;
  private HttpStatus httpStatusCode;
}
