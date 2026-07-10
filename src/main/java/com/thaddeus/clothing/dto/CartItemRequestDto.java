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
@Schema(description = "Thông tin sản phẩm cần thêm vào giỏ hàng")
public class CartItemRequestDto {

    @NotNull(message = "Mã biến thể sản phẩm không được trống")
    @Schema(
            description = "ID biến thể sản phẩm (productVariantId) — lấy từ GET /api/v1/products/{id}/variants. " +
                    "Đây là ID của biến thể cụ thể (ví dụ: Áo Polo Đỏ Size M), không phải ID sản phẩm cha. " +
                    "Sai ID sẽ gây lỗi 404 PRD_001.",
            example = "101",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long productVariantId;

    @NotNull(message = "Số lượng sản phẩm không được trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    @Schema(
            description = "Số lượng sản phẩm muốn thêm vào giỏ. Tối thiểu là 1. " +
                    "Nếu sản phẩm đã có trong giỏ, số lượng sẽ được cộng thêm (không thay thế). " +
                    "Hệ thống kiểm tra tồn kho — nếu không đủ sẽ trả về lỗi INV_001.",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer quantity;
}
