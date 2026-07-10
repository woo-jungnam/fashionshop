package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO phản hồi thông tin hồ sơ chi tiết của người dùng")
public class UserProfileResponseDto {

    @Schema(description = "ID duy nhất của người dùng", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Địa chỉ email (dùng làm username đăng nhập)", example = "user@example.com")
    private String email;

    @Schema(description = "Số điện thoại di động", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Ngày sinh", example = "1995-12-30")
    private LocalDate dob;

    @Schema(description = "Giới tính (MALE, FEMALE, OTHER)", example = "MALE")
    private String gender;

    @Schema(description = "URL ảnh đại diện", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "Danh sách các vai trò (roles) được gán cho người dùng", example = "[\"ROLE_CUSTOMER\"]")
    private Set<String> roles;
}
