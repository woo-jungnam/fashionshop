package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tạo mới hoặc cập nhật danh mục sản phẩm")
public class CategoryRequestDto {

    @NotBlank(message = "Tên danh mục không được trống")
    @Schema(
            description = "Tên hiển thị của danh mục trên website. Không được để trống, không chỉ chứa khoảng trắng.",
            example = "Áo Nam",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @NotBlank(message = "Slug danh mục không được trống")
    @Schema(
            description = "Đường dẫn URL thân thiện SEO cho danh mục — chỉ chữ thường, số và dấu gạch ngang. " +
                    "Phải là duy nhất. Ví dụ đúng: 'ao-nam', 'quan-nu', 'phu-kien'.",
            example = "ao-nam",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String slug;

    @Schema(
            description = "ID danh mục cha (tuỳ chọn). Dùng khi tạo danh mục con. Để null nếu là danh mục gốc (cấp 1). " +
                    "Ví dụ: 'Áo Polo' là con của 'Áo Nam' (parentId = 1).",
            example = "1",
            nullable = true
    )
    private Long parentId;
}
