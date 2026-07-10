package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin cấu hình hệ thống trả về")
public class SettingResponseDto {

    @Schema(description = "ID duy nhất của cấu hình.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Khóa định danh cấu hình.", example = "shipping.free_threshold")
    private String key;

    @Schema(description = "Giá trị cấu hình (luôn là chuỗi).", example = "500000", nullable = true)
    private String value;

    @Schema(description = "Mô tả ý nghĩa của setting.", example = "Ngưỡng miễn phí vận chuyển (VND)", nullable = true)
    private String description;
}
