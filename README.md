# Spring Boot JWT & Security Template

> Đây là dự án mẫu (Template) Spring Boot được cấu hình sẵn hệ thống xác thực **JWT (JSON Web Token)**, **Spring Security**, **Global Exception Handler**, **CORS** và **Swagger UI (OpenAPI)**. 
> Template này giúp bạn nhanh chóng triển khai các dự án mới mà không cần tốn thời gian cấu hình lại phần bảo mật và xác thực.

---

## 🛠️ Tech Stack & Thư viện sử dụng

| Thành phần | Công nghệ / Thư viện | Phiên bản | Mô tả |
| :--- | :--- | :--- | :--- |
| **Framework** | Spring Boot | `3.3.6` | Framework chính |
| **Language** | Java | `17` | Phiên bản Java khuyến nghị |
| **Build Tool** | Maven | — | Quản lý dự án & dependencies |
| **Database** | MySQL | — | Hệ quản trị cơ sở dữ liệu |
| **Security** | Spring Security | `OAuth2 Resource Server` | Cấu hình filter chain & JWT decoder |
| **JWT Library** | Nimbus JOSE-JWT | — | Mã hóa/giải mã JWT (thuộc Resource Server) |
| **ORM** | Spring Data JPA | Hibernate | Quản lý và giao tiếp với Database |
| **API Docs** | SpringDoc OpenAPI | `2.5.0` (Swagger UI) | Tự động sinh tài liệu API & thử nghiệm trực tiếp |
| **Filter Query** | Spring Filter | `3.1.7` | Hỗ trợ tìm kiếm, lọc dữ liệu JPA động từ URL |
| **Boilerplate** | Lombok | — | Tự động tạo Getter, Setter, Constructor... |

---

## 🚀 Hướng Dẫn Cài Đặt và Cấu Hình Khi Tái Sử Dụng

Khi clone hoặc tái sử dụng dự án template này làm nền tảng cho dự án mới, hãy thực hiện theo đúng các bước sau đây:

### Bước 1 — Refactor Package Gốc (Tùy chọn nhưng khuyến nghị)
Mặc định dự án đang sử dụng package gốc là `com.template.manhpt`. Để thay đổi thành package của dự án mới:
1. Mở dự án trong IDE (IntelliJ IDEA được khuyến nghị).
2. Chuột phải vào thư mục `com.template.manhpt` ở mục `src/main/java`.
3. Chọn **Refactor** -> **Rename** -> **Rename package**.
4. Nhập tên package mới mong muốn (ví dụ: `com.company.project`) và chọn **Refactor** trên toàn dự án để tự động cập nhật mọi câu lệnh import.

### Bước 2 — Khởi tạo Database
Dự án được cấu hình kết nối mặc định tới Database `template_db`. 
1. Khởi tạo một cơ sở dữ liệu MySQL mới:
   ```sql
   CREATE DATABASE ten_database_cua_ban CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Mở file [init_db.sql](file:///d:/IT/template/templateJWT/src/main/resources/init_db.sql), thay đổi câu lệnh `USE template_db;` ở dòng 2 thành tên database của bạn, sau đó chạy toàn bộ script để tạo cấu trúc bảng `user` chuẩn.
3. Mở file [seed_test_data.sql](file:///d:/IT/template/templateJWT/src/main/resources/seed_test_data.sql), thay đổi câu lệnh `USE template_db;` thành tên database của bạn, sau đó chạy script để nạp 3 tài khoản kiểm thử mặc định (Mật khẩu đều là `123456`):
   * **Nhân viên Bán hàng**: `sale01` (Role: `ROLE_SALE`)
   * **Nhân viên CSKH**: `cs01` (Role: `ROLE_CS`)
   * **Nhân viên Kho**: `wh01` (Role: `ROLE_WH`)

### Bước 3 — Cấu hình File Connection & Secrets
1. Mở file [application.properties](file:///d:/IT/template/templateJWT/src/main/resources/application.properties) tại mục `spring.datasource.url` và cập nhật lại tên database của bạn:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ten_database_cua_ban
   ```
2. Tạo mới hoặc chỉnh sửa file [application-secret.properties](file:///d:/IT/template/templateJWT/src/main/resources/application-secret.properties) tại thư mục `src/main/resources/` (đã được cấu hình `.gitignore` để không bị đẩy lên Git) và điền các thông tin bảo mật sau:
   ```properties
   spring.datasource.password=mat_khau_mysql_cua_ban
   jwt.base64-secret=khoa_bi_mat_base64_it_nhat_64_ky_tu
   ```
   > [!TIP]
   > **Tạo nhanh một JWT base64-secret dài (trên PowerShell):**
   > ```powershell
   > [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("day-la-khoa-bi-mat-cuc-ky-dai-va-an-toan-cho-thuat-toan-jwt-hmac-sha512-dung-cho-template"))
   > ```

### Bước 4 — Khởi động dự án
Sử dụng Maven để chạy ứng dụng:
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

---

## 📂 Cấu Trúc Dự Án Chi Tiết

Mã nguồn được tổ chức theo cấu trúc package chuẩn hóa và tách biệt nghiệp vụ rõ ràng:

```
com.template.manhpt/
│
├── ClothingShopPosApplication.java             ← Điểm khởi chạy ứng dụng (Entry Point)
│
├── config/                                     ← Các lớp cấu hình hệ thống
│   ├── AuditConfig.java                        │  Cấu hình JPA Auditing (Tự động điền ID người dùng tạo/sửa - nếu dùng)
│   ├── CorsConfig.java                         │  Cấu hình CORS (Cho phép kết nối từ localhost:3000, 5173...)
│   ├── CustomAuthenticationEntryPoint.java     │  Custom định dạng phản hồi lỗi 401 Unauthorized theo JSON chuẩn
│   ├── DateTimeFormatConfiguration.java        │  Cấu hình định dạng ngày giờ toàn cục (ISO-8601)
│   ├── OpenAPIConfig.java                      │  Cấu hình Swagger UI hỗ trợ xác thực Bearer Token
│   ├── PermissionInterceptor.java              │  Interceptor kiểm tra tài khoản hoạt động (Active) trên mỗi request
│   ├── PermissionInterceptorConfiguration.java │  Đăng ký Interceptor vào luồng xử lý Spring MVC
│   ├── SecurityConfiguration.java              │  Cấu hình Spring Security: Filter Chain, giải mã JWT, password encoder
│   ├── StaticResourcesWebConfiguration.java    │  Ánh xạ tài nguyên tĩnh (Upload files) phục vụ qua /storage/**
│   └── UserDetailsCustom.java                  │  Nạp thông tin đăng nhập từ cơ sở dữ liệu cho Spring Security
│
├── common/                                     ← Cấu trúc phản hồi và xử lý lỗi dùng chung toàn cục
│   ├── exception/
│   │   ├── BadRequestException.java            │  Lỗi dữ liệu đầu vào không hợp lệ (400)
│   │   ├── BusinessException.java              │  Lớp ngoại lệ cha phục vụ nghiệp vụ logic
│   │   ├── GlobalExceptionHandler.java         │  Bắt toàn bộ các lỗi phát sinh để trả về định dạng RestResponse JSON
│   │   ├── IdInvalidException.java             │  Lỗi mã ID không hợp lệ hoặc không tồn tại (400)
│   │   ├── PermissionException.java            │  Lỗi từ chối quyền truy cập (403)
│   │   └── ResourceNotFoundException.java      │  Lỗi không tìm thấy tài nguyên trong DB (404)
│   └── response/
│       ├── RestResponse.java                   │  Định dạng JSON chuẩn trả về Client {statusCode, error, message, data}
│       └── ResultPaginationDTO.java            │  Định dạng phân trang chuẩn cho các danh sách {meta, result}
│
├── auth/                                       ← Nghiệp vụ Đăng nhập & Xác thực
│   ├── controller/
│   │   └── AuthController.java                 │  Endpoints xử lý login, register, logout, refresh, get account
│   ├── service/
│   │   └── AuthService.java                    │  Logic xác thực tài khoản, so khớp password hash, lưu refresh token
│   └── DTO/
│       ├── ReqLoginDTO.java                    │  Dữ liệu đăng nhập gửi lên {username, password}
│       ├── ReqRegisterDTO.java                 │  Dữ liệu đăng ký tài khoản mới gửi lên
│       └── ResLoginDTO.java                    │  Dữ liệu trả về sau đăng nhập {access_token, user}
│
├── user/                                       ← Nghiệp vụ quản lý Người dùng
│   ├── entity/
│   │   └── User.java                           │  Entity User chứa các trường thông tin người dùng và RoleEnum
│   ├── repository/
│   │   └── UserRepository.java                 │  Tương tác cơ sở dữ liệu bảng user
│   └── service/
│       └── UserService.java                    │  Logic xử lý CRUD User, tạo mới, băm password, quản lý refresh token
│
└── util/                                       ← Tiện ích hỗ trợ
    ├── SecurityUtil.java                       │  Cung cấp các hàm sinh Access Token, Refresh Token, lấy User ID hiện tại
    ├── FormatRestResponse.java                 │  Controller Advice tự động bọc mọi Response thành RestResponse chuẩn
    ├── annotation/
    │   └── ApiMessage.java                     │  Annotation cấu hình thông điệp (Message) trả về cho mỗi API
    └── constant/
        ├── GenderEnum.java                     │  Enum định nghĩa giới tính: MALE, FEMALE, OTHER
        └── RoleEnum.java                       │  Enum phân quyền: ROLE_SALE, ROLE_CS, ROLE_WH
```

---

## 🔐 Phân Quyền Bằng Annotation (Method Security)

Dự án đã được bật sẵn tính năng phân quyền theo phương thức với `@EnableMethodSecurity(securedEnabled = true)` tại [SecurityConfiguration.java](file:///d:/IT/template/templateJWT/src/main/java/com/template/manhpt/config/SecurityConfiguration.java).

Để giới hạn quyền truy cập một API cho một số vai trò cụ thể, bạn chỉ cần sử dụng annotation `@PreAuthorize` trên method của Controller. 

**Ví dụ thực tế:**
```java
// Chỉ người dùng có vai trò ROLE_WH (Kho) mới được phép gọi API này
@PostMapping("/import-stock")
@PreAuthorize("hasRole('ROLE_WH')")
@ApiMessage("Nhập kho hàng thành công")
public ResponseEntity<?> importStock(@RequestBody StockRequest req) {
    return ResponseEntity.ok(stockService.process(req));
}

// Cho phép nhiều vai trò cùng truy cập (Bán hàng HOẶC Kho)
@GetMapping("/inventory")
@PreAuthorize("hasAnyRole('ROLE_SALE', 'ROLE_WH')")
@ApiMessage("Lấy thông tin tồn kho thành công")
public ResponseEntity<?> getInventory() {
    return ResponseEntity.ok(inventoryService.getAll());
}
```

---

## 📝 Quy Tắc Phát Triển Bắt Buộc (Coding Guidelines)

1. **Đặt tên logic**: Tên class, tên biến, phương thức và API endpoint bắt buộc viết bằng **tiếng Anh** chuẩn, ngắn gọn và tường minh.
2. **API Endpoint Convention**: Sử dụng danh từ số nhiều làm tài nguyên. Ví dụ: `/api/v1/products` thay vì `/api/v1/getProduct` hay `/api/v1/product/all`.
3. **Mã hóa mật khẩu**: Tuyệt đối không lưu mật khẩu dạng plain-text. Luôn băm mật khẩu bằng `BCryptPasswordEncoder` (được inject tự động qua Spring Security).
4. **Validation**: Validate tất cả các dữ liệu truyền vào Controller bằng annotation của gói `jakarta.validation.constraints` kết hợp `@Valid` tại API request body.
5. **Viết JavaDoc đầy đủ**: Bắt buộc viết tài liệu JavaDoc giải thích chức năng, tham số truyền vào (`@param`) và giá trị trả về (`@return`) đối với mọi method xử lý nghiệp vụ trong các lớp Service.
6. **Xử lý giao dịch**: Sử dụng `@Transactional` của Spring Framework đối với các phương thức nghiệp vụ thực hiện ghi/sửa dữ liệu trên nhiều bảng khác nhau để đảm bảo tính toàn vẹn dữ liệu (Atomicity).
