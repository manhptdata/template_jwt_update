package com.template.manhpt.config;

import com.template.manhpt.common.exception.PermissionException;
import com.template.manhpt.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor kiểm tra vai trò (role) của người dùng trước mỗi request.
 * Hiện tại chỉ kiểm tra user có active không thông qua JWT (không gọi Database).
 * Team có thể mở rộng thêm logic phân quyền theo role tại đây.
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        String currentUsername = SecurityUtil.getCurrentUserLogin().orElse("");
        if (currentUsername.isEmpty()) {
            return true; // Chưa đăng nhập → để SecurityFilterChain xử lý
        }

        Boolean isActive = SecurityUtil.isCurrentUserActive();
        if (isActive == null) {
            throw new PermissionException("Không tìm thấy thông tin người dùng trong token");
        }
        if (!isActive) {
            throw new PermissionException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }

        // TODO: Team mở rộng logic phân quyền theo role tại đây
        // Ví dụ: kiểm tra JWT có quyền truy cập requestURI không

        return true;
    }
}
