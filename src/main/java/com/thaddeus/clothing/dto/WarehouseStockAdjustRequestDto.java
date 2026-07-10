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
@Schema(description = "Yêu cầu thay đổi tồn kho (Nhập/Xuất kho)")
public class WarehouseStockAdjustRequestDto {

    @NotNull(message = "Mã kho không được trống")
    @Schema(
            description = "ID kho hàng thực hiện giao dịch.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long warehouseId;

    @NotNull(message = "Mã biến thể không được trống")
    @Schema(
            description = "ID biến thể sản phẩm cần điều chỉnh tồn kho.",
            example = "101",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long productVariantId;

    @NotNull(message = "Số lượng thay đổi không được trống")
    @Min(value = 1, message = "Số lượng thay đổi tối thiểu là 1")
    @Schema(
            description = "Số lượng sản phẩm nhập/xuất kho. Tối thiểu là 1.",
            example = "50",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer quantity;
}
