package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Bảng hướng dẫn size sản phẩm — giúp khách hàng chọn đúng size trước khi mua")
public class SizeGuideResponseDto {

    @Schema(description = "ID duy nhất của bảng size.", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Tên bảng size, thường theo loại sản phẩm.", example = "Bảng Size Áo Nam")
    private String name;

    @Schema(description = "URL hình ảnh bảng size (thường là ảnh chứa bảng số đo chi tiết).", example = "https://cdn.thaddeus.vn/size-guide/ao-nam-2026.jpg", nullable = true)
    private String imageUrl;

    @Schema(
            description = "Thông số size chi tiết dạng JSON string hoặc văn bản. " +
                    "Ví dụ: '{\"S\": {\"ngực\": \"88cm\", \"eo\": \"76cm\"}, \"M\": {\"ngực\": \"92cm\", \"eo\": \"80cm\"}}'",
            example = "{\"S\": {\"ngực\": \"88cm\", \"eo\": \"76cm\"}, \"M\": {\"ngực\": \"92cm\", \"eo\": \"80cm\"}, \"L\": {\"ngực\": \"96cm\", \"eo\": \"84cm\"}}",
            nullable = true
    )
    private String specifications;
}
