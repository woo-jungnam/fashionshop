package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết sản phẩm trả về từ hệ thống")
public class ProductResponseDto {

    @Schema(description = "ID duy nhất của sản phẩm trong hệ thống — dùng cho update, delete, lấy variants/images.", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Tên hiển thị của sản phẩm.", example = "Áo Polo Nam Cổ Bẻ Basic Premium")
    private String name;

    @Schema(description = "Mã SKU cha — định danh nội bộ duy nhất cho nhóm sản phẩm.", example = "POLO-BASIC-PREM-001")
    private String parentSku;

    @Schema(description = "Đường dẫn URL SEO-friendly của sản phẩm.", example = "ao-polo-nam-co-be-basic-premium")
    private String slug;

    @Schema(description = "Mô tả ngắn hiển thị trong danh sách sản phẩm.", example = "Cotton Pima 100%, co giãn 4 chiều", nullable = true)
    private String shortDescription;

    @Schema(description = "Mô tả chi tiết đầy đủ, có thể chứa HTML.", example = "<p>Áo Polo cao cấp...</p>", nullable = true)
    private String description;

    @Schema(description = "Chất liệu vải/vật liệu sản phẩm.", example = "100% Cotton Pima", nullable = true)
    private String material;

    @Schema(description = "Hướng dẫn giặt và bảo quản.", example = "Giặt máy ≤30°C, không tẩy, phơi bóng mát", nullable = true)
    private String careInstructions;

    @Schema(description = "ID danh mục sản phẩm.", example = "1")
    private Long categoryId;

    @Schema(description = "Tên danh mục sản phẩm (join từ DB).", example = "Áo Nam")
    private String categoryName;

    @Schema(description = "ID thương hiệu — null nếu hàng no-brand.", example = "1", nullable = true)
    private Long brandId;

    @Schema(description = "Tên thương hiệu (join từ DB) — null nếu hàng no-brand.", example = "Canifa", nullable = true)
    private String brandName;

    @Schema(
            description = "Trạng thái sản phẩm:\n- `ACTIVE`: Đang bán\n- `INACTIVE`: Ngừng bán\n- `DRAFT`: Nháp",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "DRAFT"}
    )
    private String status;
}
