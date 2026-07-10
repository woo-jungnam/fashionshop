package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin hình ảnh của sản phẩm")
public class ProductImageResponseDto {

    @Schema(description = "ID duy nhất của ảnh sản phẩm.", example = "201", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "URL đường dẫn tới file ảnh sản phẩm (CDN hoặc storage). Frontend dùng URL này để hiển thị ảnh.", example = "https://cdn.thaddeus.vn/products/polo-basic-001-main.jpg")
    private String imageUrl;

    @Schema(description = "Thứ tự hiển thị ảnh trong gallery — số nhỏ hơn hiển thị trước. Ảnh có displayOrder = 1 thường là ảnh đại diện chính.", example = "1")
    private Integer displayOrder;

    @Schema(description = "true nếu đây là ảnh đại diện chính (thumbnail) của sản phẩm — hiển thị trong danh sách và kết quả tìm kiếm.", example = "true")
    private boolean isMain;
}
