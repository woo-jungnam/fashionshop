package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết kho hàng trả về từ hệ thống")
public class WarehouseResponseDto {

    @Schema(description = "ID duy nhất của kho hàng.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Tên hiển thị của kho hàng.", example = "Kho trung tâm Hà Nội")
    private String name;

    @Schema(description = "Địa chỉ của kho hàng.", example = "Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội", nullable = true)
    private String address;

    @Schema(description = "Loại kho hàng.", example = "MAIN", nullable = true)
    private String warehouseType;
}
