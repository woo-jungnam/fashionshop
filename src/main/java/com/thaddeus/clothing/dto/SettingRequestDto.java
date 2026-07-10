package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tạo mới hoặc cập nhật cấu hình hệ thống (key-value settings)")
public class SettingRequestDto {

    @NotBlank(message = "Key cấu hình không được trống")
    @Schema(
            description = "Khóa định danh cấu hình (unique key). Dùng quy tắc snake_case hoặc dot.notation. " +
                    "Nếu key đã tồn tại, giá trị sẽ được cập nhật (upsert). " +
                    "Ví dụ: 'site.name', 'shipping.free_threshold', 'payment.sepay.enabled'.",
            example = "shipping.free_threshold",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String key;

    @Schema(
            description = "Giá trị của cấu hình — luôn lưu dưới dạng chuỗi, ứng dụng tự parse khi đọc. " +
                    "Ví dụ: '500000' cho số tiền, 'true'/'false' cho boolean, 'Thaddeus Clothing' cho tên.",
            example = "500000",
            nullable = true
    )
    private String value;

    @Schema(
            description = "Mô tả ý nghĩa của cấu hình — giúp Admin hiểu tác dụng của setting này.",
            example = "Ngưỡng giá trị đơn hàng được miễn phí vận chuyển (VND). Đơn >= giá trị này sẽ được ship miễn phí.",
            nullable = true
    )
    private String description;
}
