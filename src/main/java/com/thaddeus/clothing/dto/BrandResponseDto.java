package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin thương hiệu trả về từ hệ thống")
public class BrandResponseDto {

    @Schema(description = "ID duy nhất của thương hiệu.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Tên hiển thị của thương hiệu.", example = "Nike")
    private String name;

    @Schema(description = "Đường dẫn URL ảnh logo của thương hiệu.", example = "https://example.com/logos/nike.png", nullable = true)
    private String logoUrl;

    @Schema(description = "Xuất xứ của thương hiệu.", example = "USA", nullable = true)
    private String origin;
}
