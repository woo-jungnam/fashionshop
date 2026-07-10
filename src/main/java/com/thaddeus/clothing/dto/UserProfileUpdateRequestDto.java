package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO yêu cầu cập nhật thông tin cá nhân của người dùng")
public class UserProfileUpdateRequestDto {

    @NotBlank(message = "Họ tên không được trống")
    @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Văn A", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Schema(description = "Số điện thoại liên lạc", example = "0987654321", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "Ngày sinh (YYYY-MM-DD)", example = "1995-12-30", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate dob;

    @Schema(description = "Giới tính (MALE, FEMALE, OTHER)", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gender;

    @Schema(description = "Đường dẫn ảnh đại diện", example = "https://example.com/avatar.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String avatarUrl;
}
