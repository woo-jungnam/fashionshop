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
@Schema(description = "Thông tin nhập hàng vào kho (stock-in). Dùng để tăng số lượng tồn kho tại một kho cụ thể.")
public class StockUpdateRequestDto {

    @NotNull(message = "Mã kho không được trống")
    @Schema(
            description = "ID kho hàng cần nhập thêm tồn kho. Kho phải tồn tại trong hệ thống.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long warehouseId;

    @NotNull(message = "Mã biến thể không được trống")
    @Schema(
            description = "ID biến thể sản phẩm cần nhập kho — lấy từ GET /api/v1/products/{id}/variants. " +
                    "Biến thể phải tồn tại và đang ACTIVE.",
            example = "101",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long productVariantId;

    @NotNull(message = "Số lượng thay đổi không được trống")
    @Min(value = 1, message = "Số lượng thay đổi tối thiểu là 1")
    @Schema(
            description = "Số lượng sản phẩm nhập thêm vào kho. Phải tối thiểu là 1. " +
                    "Hệ thống sẽ cộng dồn vào số lượng hiện có (không ghi đè). " +
                    "Ví dụ: tồn kho hiện tại 50, nhập thêm 100 → tồn kho mới = 150.",
            example = "100",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer quantity;
}
