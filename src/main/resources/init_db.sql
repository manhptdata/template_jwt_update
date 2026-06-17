CREATE DATABASE IF NOT EXISTS clothing_shop_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clothing_shop_db;

-- Xóa bảng nếu đã tồn tại để tránh lỗi khi chạy lại
DROP TABLE IF EXISTS `user`;

-- Tạo bảng user
CREATE TABLE `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(15) UNIQUE,
    `role` VARCHAR(50) NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `refresh_token` TEXT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
