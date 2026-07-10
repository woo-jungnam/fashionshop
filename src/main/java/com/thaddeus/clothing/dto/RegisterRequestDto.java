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
@Schema(description = "Thông tin đăng ký tài khoản mới. Sau khi đăng ký thành công, dùng API /auth/login để lấy JWT.")
public class RegisterRequestDto {

    @NotBlank(message = "Email đăng ký không được trống")
    @Email(message = "Định dạng email không hợp lệ")
    @Schema(
            description = "Địa chỉ email dùng làm username đăng nhập — phải là duy nhất trong toàn hệ thống. Không thể thay đổi sau khi đăng ký.",
            example = "nguyen.van.a@gmail.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    @Schema(
            description = "Mật khẩu tài khoản. Được mã hóa bằng BCrypt trước khi lưu — không bao giờ lưu mật khẩu thô. " +
                    "Khuyến nghị: tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.",
            example = "MySecure@2026",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotBlank(message = "Họ tên không được trống")
    @Schema(
            description = "Họ và tên đầy đủ của người dùng. Hiển thị trên hồ sơ cá nhân và đơn hàng.",
            example = "Nguyễn Văn A",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String fullName;

    @Schema(
            description = "Số điện thoại di động liên lạc (không bắt buộc). " +
                    "Dùng để xác nhận đơn hàng và liên lạc giao hàng. Khuyến nghị nhập đúng định dạng Việt Nam: 0xxxxxxxxx.",
            example = "0901234567",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    private String phoneNumber;
}
