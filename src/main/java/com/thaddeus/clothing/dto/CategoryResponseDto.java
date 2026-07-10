package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin danh mục sản phẩm trả về từ hệ thống")
public class CategoryResponseDto {

    @Schema(description = "ID duy nhất của danh mục — dùng làm `categoryId` khi tạo sản phẩm.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Tên hiển thị của danh mục.", example = "Áo Nam")
    private String name;

    @Schema(description = "Đường dẫn URL SEO-friendly của danh mục.", example = "ao-nam")
    private String slug;

    @Schema(description = "ID danh mục cha — null nếu đây là danh mục gốc cấp 1.", example = "null", nullable = true)
    private Long parentId;
}
