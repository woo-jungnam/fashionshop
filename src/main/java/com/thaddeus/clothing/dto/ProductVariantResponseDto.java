package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin một biến thể sản phẩm (phân biệt theo size, màu sắc). " +
        "Dùng `id` của biến thể này làm `productVariantId` khi thêm vào giỏ hàng hoặc tạo đơn hàng.")
public class ProductVariantResponseDto {

    @Schema(description = "ID duy nhất của biến thể — đây là `productVariantId` cần truyền khi thêm vào giỏ hàng (POST /api/v1/carts/items).", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Mã SKU biến thể — định danh duy nhất cho từng biến thể size/màu. Format: {parentSku}-{COLOR}-{SIZE}.", example = "POLO-BASIC-PREM-001-RED-M")
    private String sku;

    @Schema(description = "Mã vạch sản phẩm (barcode) — dùng trong quản lý kho.", example = "8938505970001", nullable = true)
    private String barcode;

    @Schema(description = "Giá gốc trước khuyến mãi (đơn vị: VND).", example = "299000")
    private BigDecimal price;

    @Schema(description = "Giá khuyến mãi (đơn vị: VND). Null nếu không có khuyến mãi. Frontend nên hiển thị giá này nếu khác null.", example = "249000", nullable = true)
    private BigDecimal salePrice;

    @Schema(
            description = "Trạng thái biến thể:\n- `ACTIVE`: Đang bán\n- `INACTIVE`: Ngừng bán\n- `OUT_OF_STOCK`: Hết hàng tạm thời",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "OUT_OF_STOCK"}
    )
    private String status;

    @Schema(
            description = "Danh sách các thuộc tính phân biệt biến thể này. " +
                    "Format: 'TênThuộcTính: GiáTrị'. " +
                    "Dùng để hiển thị nút chọn size/màu trên UI.",
            example = "[\"Color: Đỏ\", \"Size: M\"]"
    )
    private Set<String> attributes;
}
