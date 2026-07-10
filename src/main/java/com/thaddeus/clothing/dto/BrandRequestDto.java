package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tạo mới hoặc cập nhật thương hiệu")
public class BrandRequestDto {

    @NotBlank(message = "Tên thương hiệu không được trống")
    @Size(max = 255, message = "Tên thương hiệu tối đa 255 ký tự")
    @Schema(
            description = "Tên hiển thị của thương hiệu. Phải là duy nhất, không được để trống.",
            example = "Nike",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Đường dẫn URL ảnh logo của thương hiệu.",
            example = "https://example.com/logos/nike.png",
            nullable = true
    )
    private String logoUrl;

    @Schema(
            description = "Xuất xứ của thương hiệu.",
            example = "USA",
            nullable = true
    )
    private String origin;
}
