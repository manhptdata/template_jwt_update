package com.template.manhpt.auth.DTO;

import com.template.manhpt.util.constant.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRegisterDTO {

    @NotBlank(message = "username không được để trống")
    private String username;

    @NotBlank(message = "password không được để trống")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String phone;

    @NotNull(message = "Vai trò không được để trống")
    private RoleEnum role;
}
