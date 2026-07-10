package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tạo mới hoặc cập nhật kho hàng")
public class WarehouseRequestDto {

    @NotBlank(message = "Tên kho hàng không được trống")
    @Schema(
            description = "Tên hiển thị của kho hàng. Phải là duy nhất, không được để trống.",
            example = "Kho trung tâm Hà Nội",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Địa chỉ vật lý của kho hàng.",
            example = "Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội",
            nullable = true
    )
    private String address;

    @Schema(
            description = "Loại kho hàng (ví dụ: MAIN, RETAIL, TRANSIT...).",
            example = "MAIN",
            nullable = true
    )
    private String warehouseType;
}
