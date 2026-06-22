-- =============================================
-- Chạy SAU KHI đã tạo DB bằng script chính
-- Tạo 3 tài khoản test (Password tất cả: 123456)
-- =============================================

USE template_db;

-- Insert 3 tài khoản test (password: 123456)
INSERT INTO user (username, password_hash, full_name, phone, role, is_active) VALUES
('sale01', '$2a$10$26IhRLEHb3p5ZGoxsLiyCevhGyJyxhgF/eGszXGQyJxFH.QQJD9ya', 'NV Ban Hang',  '0901111111', 'ROLE_SALE', TRUE),
('cs01',   '$2a$10$26IhRLEHb3p5ZGoxsLiyCevhGyJyxhgF/eGszXGQyJxFH.QQJD9ya', 'NV CSKH',      '0902222222', 'ROLE_CS',   TRUE),
('wh01',   '$2a$10$26IhRLEHb3p5ZGoxsLiyCevhGyJyxhgF/eGszXGQyJxFH.QQJD9ya', 'NV Kho',       '0903333333', 'ROLE_WH',   TRUE);
