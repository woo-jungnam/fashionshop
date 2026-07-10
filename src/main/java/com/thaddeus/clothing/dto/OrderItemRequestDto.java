package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Một dòng sản phẩm trong đơn hàng")
public class OrderItemRequestDto {

    @NotNull(message = "Mã biến thể sản phẩm không được trống")
    @Schema(
            description = "ID biến thể sản phẩm — lấy từ GET /api/v1/products/{id}/variants. " +
                    "Phải là biến thể đang ACTIVE, không phải ID sản phẩm cha.",
            example = "101",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long productVariantId;

    @NotNull(message = "Số lượng sản phẩm không được trống")
    @Min(value = 1, message = "Số lượng đặt hàng tối thiểu là 1")
    @Schema(
            description = "Số lượng đặt hàng của biến thể này. Tối thiểu là 1. " +
                    "Hệ thống kiểm tra số lượng tồn kho — nếu không đủ sẽ trả về 400 với mã lỗi INV_001.",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer quantity;
}
