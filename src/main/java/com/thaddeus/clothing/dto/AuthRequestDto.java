package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin đăng nhập hệ thống")
public class AuthRequestDto {

    @NotBlank(message = "Email đăng nhập không được trống")
    @Email(message = "Định dạng email không hợp lệ")
    @Schema(
            description = "Địa chỉ email đã đăng ký trong hệ thống — dùng làm username đăng nhập. Phải đúng định dạng email.",
            example = "admin@thaddeus.vn",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    @Schema(
            description = "Mật khẩu tài khoản. Phân biệt chữ hoa/thường. Không hiển thị trong response và không bao giờ được log.",
            example = "Admin@2026!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
