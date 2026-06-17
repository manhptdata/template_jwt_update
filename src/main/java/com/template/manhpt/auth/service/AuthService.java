package com.template.manhpt.auth.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.template.manhpt.auth.DTO.ReqLoginDTO;
import com.template.manhpt.auth.DTO.ReqRegisterDTO;
import com.template.manhpt.auth.DTO.ResLoginDTO;
import com.template.manhpt.common.exception.IdInvalidException;
import com.template.manhpt.entity.User;
import com.template.manhpt.user.service.UserService;
import com.template.manhpt.util.SecurityUtil;
import com.template.manhpt.util.constant.RoleEnum;

@Service
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthService(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    public ResLoginDTO login(ReqLoginDTO loginRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = userService.getUserByUsername(loginRequest.getUsername());
        ResLoginDTO responseDTO = buildLoginResponse(currentUser);

        String accessToken = securityUtil.createAccessToken(currentUser.getUsername(), responseDTO);
        responseDTO.setAccessToken(accessToken);

        return responseDTO;
    }

    public String createAndSaveRefreshToken(String username, ResLoginDTO responseDTO) {
        String refreshToken = securityUtil.createRefreshToken(username, responseDTO);
        userService.updateRefreshToken(refreshToken, username);
        return refreshToken;
    }

    public ResLoginDTO.UserLogin register(ReqRegisterDTO dto) throws IdInvalidException {
        if (userService.existsByUsername(dto.getUsername())) {
            throw new IdInvalidException("Username đã tồn tại");
        }
        
        if (dto.getPhone() != null && !dto.getPhone().isEmpty() && userService.existsByPhone(dto.getPhone())) {
            throw new IdInvalidException("Số điện thoại đã tồn tại");
        }

        if (dto.getRole() == null) {
            throw new IdInvalidException("Vai trò không hợp lệ hoặc không được để trống");
        }

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPasswordHash(dto.getPassword());
        newUser.setFullName(dto.getFullName());
        newUser.setPhone(dto.getPhone());
        newUser.setRole(dto.getRole());
        newUser.setActive(true);
        
        newUser = userService.createUser(newUser);
        
        return new ResLoginDTO.UserLogin(newUser.getId(), newUser.getUsername(),
                newUser.getFullName(), newUser.getRole().name(), newUser.isActive());
    }

    public ResLoginDTO refresh(String refreshTokenCookie) throws IdInvalidException {
        if (refreshTokenCookie.isEmpty()) {
            throw new IdInvalidException("Refresh token không tồn tại. Vui lòng đăng nhập lại.");
        }

        Jwt decodedToken = securityUtil.checkValidRefreshToken(refreshTokenCookie);
        String usernameFromToken = decodedToken.getSubject();

        User userFromDB = userService.getUserByRefreshTokenAndUsername(refreshTokenCookie, usernameFromToken);
        if (userFromDB == null) {
            throw new IdInvalidException("Refresh token không hợp lệ. Vui lòng đăng nhập lại.");
        }

        ResLoginDTO responseDTO = buildLoginResponse(userFromDB);
        String newAccessToken = securityUtil.createAccessToken(usernameFromToken, responseDTO);
        responseDTO.setAccessToken(newAccessToken);

        return responseDTO;
    }

    public void logout(String currentUsername) {
        if (currentUsername != null && !currentUsername.isEmpty()) {
            userService.updateRefreshToken(null, currentUsername);
        }
    }

    private ResLoginDTO buildLoginResponse(User user) {
        ResLoginDTO dto = new ResLoginDTO();
        dto.setUser(new ResLoginDTO.UserLogin(user.getId(), user.getUsername(), user.getFullName(),
                user.getRole().name(), user.isActive()));
        return dto;
    }
}
