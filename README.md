# Hệ Thống Hỗ Trợ Chẩn Đoán Viêm Phổi - Backend

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)

## 📖 Giới Thiệu

Đây là **Backend API** cho Hệ Thống Hỗ Trợ Chẩn Đoán Viêm Phổi. Dự án sử dụng **Spring Boot** cho các dịch vụ cốt lõi, đảm bảo hiệu năng cao và khả năng mở rộng, tích hợp với **PostgreSQL** để quản lý và lưu trữ dữ liệu y tế.

## 🛠 Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java 21
- **Framework:** Spring Boot 3.5.11 (Snapshot)
- **Cơ sở dữ liệu:** PostgreSQL
- **Bảo mật:** Spring Security
- **Công cụ Build:** Maven (Wrapper)

## 🚀 Bắt Đầu

### Yêu Cầu Tiên Quyết

Hãy đảm bảo bạn đã cài đặt:
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [PostgreSQL](https://www.postgresql.org/download/)

### 📦 Cài Đặt

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd BE
   ```

2. **Cài đặt Cơ sở dữ liệu**
   Tạo một database PostgreSQL mới (ví dụ: `pneumonia_db`) và cấu hình thông tin đăng nhập.

3. **Cấu hình**
   Cập nhật file `src/main/resources/application.yaml` để kết nối với database của bạn.
   
   *Ví dụ Cấu hình:*
   ```yaml
   spring:
     application:
       name: pneumonia-backend
     datasource:
       url: jdbc:postgresql://localhost:5432/pneumonia_db
       username: your_db_user # Tên đăng nhập DB của bạn
       password: your_db_password # Mật khẩu DB của bạn
     jpa:   
### ▶️ Chạy Ứng Dụng

java -jar target/demo-0.0.1-SNAPSHOT.jar

Khi ứng dụng đang chạy, bạn có thể xem tài liệu API chi tiết và thử nghiệm trực tiếp thông qua Swagger UI:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs:** `http://localhost:8080/v3/api-docs`

## 🤝 Đóng Góp

1. Fork dự án.
2. Tạo Feature Branch (`git checkout -b feature/TinhNangMoi`).
3. Commit thay đổi của bạn (`git commit -m 'Thêm tính năng mới'`).
4. Push lên Branch (`git push origin feature/TinhNangMoi`).
5. Tạo Pull Request.

## 📄 Giấy Phép

Được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm thông tin.
