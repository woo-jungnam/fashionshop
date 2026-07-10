package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin một dòng sản phẩm trong đơn hàng")
public class OrderItemResponseDto {

    @Schema(description = "ID dòng hàng trong đơn — dùng làm `orderItemId` khi viết đánh giá (POST /api/v1/reviews) hoặc tạo yêu cầu đổi trả.", example = "501", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID biến thể sản phẩm.", example = "101")
    private Long productVariantId;

    @Schema(description = "Mã SKU biến thể tại thời điểm đặt hàng.", example = "POLO-BASIC-PREM-001-RED-M")
    private String sku;

    @Schema(description = "Tên sản phẩm tại thời điểm đặt hàng.", example = "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M")
    private String productName;

    @Schema(description = "Số lượng đã đặt.", example = "2")
    private Integer quantity;

    @Schema(description = "Đơn giá tại thời điểm đặt hàng (VND) — được lưu cố định, không thay đổi kể cả khi giá sản phẩm thay đổi sau này.", example = "249000")
    private BigDecimal priceAtPurchase;
}
