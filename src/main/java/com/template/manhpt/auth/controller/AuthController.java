package com.template.manhpt.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.template.manhpt.auth.DTO.ReqLoginDTO;
import com.template.manhpt.auth.DTO.ReqRegisterDTO;
import com.template.manhpt.auth.DTO.ResLoginDTO;
import com.template.manhpt.auth.service.AuthService;
import com.template.manhpt.common.exception.IdInvalidException;
import com.template.manhpt.user.entity.User;
import com.template.manhpt.user.service.UserService;
import com.template.manhpt.util.SecurityUtil;
import com.template.manhpt.util.annotation.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller xử lý xác thực: đăng nhập, đăng ký, đăng xuất, refresh token, lấy thông tin
 * tài khoản.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Đăng nhập, đăng ký, đăng xuất, refresh token")
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	public AuthController(AuthService authService, UserService userService) {
		this.authService = authService;
		this.userService = userService;
	}

	/**
	 * Đăng nhập hệ thống.
	 *
	 * @param loginRequest DTO chứa username và password
	 * @return ResLoginDTO chứa access_token và thông tin user
	 */
	@PostMapping("/login")
	@ApiMessage("Đăng nhập thành công")
	@Operation(summary = "Đăng nhập", description = "Xác thực username + password, nhận JWT access token")
	public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginRequest) {
		ResLoginDTO responseDTO = authService.login(loginRequest);
		
		String refreshToken = authService.createAndSaveRefreshToken(loginRequest.getUsername(), responseDTO);
		ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken, 7 * 24 * 60 * 60);

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(responseDTO);
	}

	/**
	 * Đăng ký tài khoản mới.
	 *
	 * @param registerRequest DTO chứa thông tin đăng ký
	 * @return Thông tin user sau khi tạo
	 */
	@PostMapping("/register")
	@ApiMessage("Đăng kí thành công")
	@Operation(summary = "Đăng kí", description = "Tạo tài khoản mới cho hệ thống")
	public ResponseEntity<ResLoginDTO.UserLogin> register(@Valid @RequestBody ReqRegisterDTO registerRequest)
			throws IdInvalidException {
		ResLoginDTO.UserLogin userLogin = authService.register(registerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(userLogin);
	}

	/**
	 * Lấy thông tin tài khoản của người dùng đang đăng nhập.
	 *
	 * @return thông tin user hiện tại
	 */
	@GetMapping("/account")
	@ApiMessage("Lấy thông tin tài khoản thành công")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Lấy thông tin tài khoản hiện tại")
	public ResponseEntity<ResLoginDTO.UserGetAccount> getAccountInfo() {
		String currentUsername = SecurityUtil.getCurrentUserLogin().orElse("");
		User currentUser = userService.getUserByUsername(currentUsername);

		ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getUsername(),
				currentUser.getFullName(), currentUser.getRole().name(), currentUser.isActive());
		return ResponseEntity.ok(new ResLoginDTO.UserGetAccount(userLogin));
	}

	/**
	 * Cấp mới access token dựa trên refresh token trong cookie.
	 *
	 * @param refreshTokenCookie refresh token từ cookie
	 * @return ResLoginDTO với access token mới
	 */
	@GetMapping("/refresh")
	@ApiMessage("Làm mới token thành công")
	@Operation(summary = "Refresh access token bằng refresh token cookie")
	public ResponseEntity<ResLoginDTO> refreshAccessToken(
			@CookieValue(name = "refresh_token", defaultValue = "") String refreshTokenCookie)
			throws IdInvalidException {

		ResLoginDTO responseDTO = authService.refresh(refreshTokenCookie);
		
		String newRefreshToken = authService.createAndSaveRefreshToken(responseDTO.getUser().getUsername(), responseDTO);
		ResponseCookie newCookie = buildRefreshTokenCookie(newRefreshToken, 7 * 24 * 60 * 60);

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newCookie.toString()).body(responseDTO);
	}

	/**
	 * Đăng xuất — xoá refresh token trong DB và clear cookie.
	 *
	 * @return 200 OK
	 */
	@PostMapping("/logout")
	@ApiMessage("Đăng xuất thành công")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Đăng xuất")
	public ResponseEntity<Void> logout() {
		String currentUsername = SecurityUtil.getCurrentUserLogin().orElse("");
		authService.logout(currentUsername);
		
		ResponseCookie clearCookie = buildRefreshTokenCookie("", 0);
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, clearCookie.toString()).build();
	}

	// ──── Helper methods ────

	private ResponseCookie buildRefreshTokenCookie(String value, long maxAge) {
		return ResponseCookie.from("refresh_token", value).httpOnly(true).secure(true).path("/").maxAge(maxAge).build();
	}
}
