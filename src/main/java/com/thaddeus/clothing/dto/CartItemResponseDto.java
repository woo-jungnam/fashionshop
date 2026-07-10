package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin một dòng sản phẩm trong giỏ hàng")
public class CartItemResponseDto {

    @Schema(description = "ID của dòng hàng trong giỏ — dùng để cập nhật số lượng (PUT /carts/items/{itemId}) hoặc xóa khỏi giỏ (DELETE /carts/items/{itemId}).", example = "55", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID biến thể sản phẩm.", example = "101")
    private Long productVariantId;

    @Schema(description = "Mã SKU của biến thể sản phẩm.", example = "POLO-BASIC-PREM-001-RED-M")
    private String sku;

    @Schema(description = "Tên sản phẩm hiển thị trong giỏ hàng.", example = "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M")
    private String productName;

    @Schema(description = "Giá gốc của sản phẩm (VND).", example = "299000")
    private BigDecimal price;

    @Schema(description = "Giá khuyến mãi hiện tại (VND). Null nếu không có khuyến mãi. Frontend nên ưu tiên hiển thị giá này.", example = "249000", nullable = true)
    private BigDecimal salePrice;

    @Schema(description = "Số lượng sản phẩm đã chọn trong giỏ.", example = "2")
    private Integer quantity;
}
