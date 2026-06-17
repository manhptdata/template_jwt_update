package com.template.manhpt.common.exception;


import com.template.manhpt.common.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Xử lý tập trung tất cả exception trong hệ thống, trả về format RestResponse
 * chuẩn.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Xử lý lỗi validate input từ @Valid — trả về danh sách các field lỗi.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<RestResponse<Object>> handleValidationException(MethodArgumentNotValidException exception) {

		BindingResult bindingResult = exception.getBindingResult();
		List<String> errorMessages = bindingResult.getAllErrors().stream().map(error -> {
			if (error instanceof FieldError fieldError) {
				return fieldError.getField() + ": " + fieldError.getDefaultMessage();
			}
			return error.getDefaultMessage();
		}).collect(Collectors.toList());

		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("Dữ liệu đầu vào không hợp lệ");
		response.setMessage(errorMessages.size() == 1 ? errorMessages.get(0) : errorMessages);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	/**
	 * Xử lý lỗi đăng nhập sai thông tin (email/password không đúng).
	 */
	@ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
	public ResponseEntity<RestResponse<Object>> handleBadCredentials(RuntimeException exception) {
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
		response.setError("Sai thông tin đăng nhập");
		response.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	/**
	 * Xử lý lỗi ID không hợp lệ (không tìm thấy entity theo ID).
	 */
	@ExceptionHandler(IdInvalidException.class)
	public ResponseEntity<RestResponse<Object>> handleIdInvalidException(IdInvalidException exception) {
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("ID không hợp lệ");
		response.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	/**
	 * Xử lý lỗi không tìm thấy resource.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<RestResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException exception) {
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.NOT_FOUND.value());
		response.setError("Không tìm thấy dữ liệu");
		response.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	/**
	 * Xử lý lỗi không có quyền truy cập.
	 */
	@ExceptionHandler(PermissionException.class)
	public ResponseEntity<RestResponse<Object>> handlePermissionException(PermissionException exception) {
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.FORBIDDEN.value());
		response.setError("Không có quyền truy cập");
		response.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	/**
	 * Xử lý lỗi nghiệp vụ Client gửi yêu cầu sai trạng thái logic.
	 */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<RestResponse<Object>> handleBadRequestException(BadRequestException exception) {
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("Yêu cầu không hợp lệ");
		response.setMessage(exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	/**
	 * Xử lý lỗi định dạng dữ liệu (vd: sai kiểu Enum, thiếu dấy phẩy JSON, ...)
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<RestResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("Định dạng dữ liệu không hợp lệ");
		response.setMessage("Dữ liệu gửi lên không đúng định dạng (Ví dụ: sai kiểu Role Enum). Vui lòng kiểm tra lại.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	/**
	 * Xử lý các lỗi runtime không mong đợi còn lại.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestResponse<Object>> handleGenericException(Exception exception) {
		// Ở môi trường thực tế nên dùng log.error("Lỗi hệ thống:", exception) để ghi log
		RestResponse<Object> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setError("Lỗi hệ thống");
		response.setMessage("Đã có lỗi hệ thống xảy ra. Vui lòng thử lại sau.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

    /**
     * Xử lý riêng các lỗi Logic nghiệp vụ (Ví dụ: Số dư không đủ, Hết hàng...)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestResponse<Object>> handleBusinessException(BusinessException exception) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value()); // Mã 400: Yêu cầu không hợp lệ
        response.setError("Lỗi xử lý nghiệp vụ");
        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
