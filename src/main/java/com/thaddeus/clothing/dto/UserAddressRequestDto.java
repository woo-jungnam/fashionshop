package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO yêu cầu thêm mới hoặc cập nhật địa chỉ giao hàng của người dùng")
public class UserAddressRequestDto {

    @NotBlank(message = "Tên người nhận không được trống")
    @Schema(description = "Họ và tên người nhận hàng", example = "Nguyễn Văn A", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientName;

    @NotBlank(message = "Số điện thoại nhận không được trống")
    @Schema(description = "Số điện thoại liên lạc nhận hàng", example = "0987654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ chi tiết không được trống")
    @Schema(description = "Địa chỉ cụ thể (Số nhà, tên đường, ngõ hẻm)", example = "Số 123 Đường Láng", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detailAddress;

    @NotBlank(message = "Phường/Xã không được trống")
    @Schema(description = "Phường hoặc Xã", example = "Láng Hạ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ward;

    @NotBlank(message = "Quận/Huyện không được trống")
    @Schema(description = "Quận hoặc Huyện", example = "Đống Đa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được trống")
    @Schema(description = "Tỉnh hoặc Thành phố trực thuộc trung ương", example = "Hà Nội", requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    @Schema(description = "Loại địa chỉ (Ví dụ: HOME - Nhà riêng, OFFICE - Văn phòng)", example = "HOME", defaultValue = "HOME", allowableValues = {"HOME", "OFFICE"})
    private String addressType; // HOME, OFFICE

    @Schema(description = "Đánh dấu là địa chỉ giao hàng mặc định của người dùng", example = "true", defaultValue = "false")
    private boolean isDefault;
}
