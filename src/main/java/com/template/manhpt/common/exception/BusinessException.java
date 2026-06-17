package com.template.manhpt.common.exception;

/**
 * Lỗi liên quan đến logic nghiệp vụ chung của hệ thống.
 * Ví dụ: Số dư không đủ, mã giảm giá hết hạn, trùng lặp dữ liệu...
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
