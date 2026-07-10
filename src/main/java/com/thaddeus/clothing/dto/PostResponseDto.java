package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin bài viết/blog trả về")
public class PostResponseDto {

    @Schema(description = "ID duy nhất của bài viết.", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Tiêu đề bài viết.", example = "Xu hướng thời trang Hè 2026: Màu Pastel lên ngôi")
    private String title;

    @Schema(description = "Đường dẫn URL SEO-friendly của bài viết.", example = "xu-huong-thoi-trang-he-2026-mau-pastel-len-ngoi")
    private String slug;

    @Schema(description = "URL ảnh thumbnail/đại diện bài viết.", example = "https://cdn.thaddeus.vn/blog/thumbnail-pastel-2026.jpg", nullable = true)
    private String thumbnailUrl;

    @Schema(description = "Mô tả ngắn bài viết — hiển thị trong danh sách blog.", example = "Khám phá những gam màu pastel dịu dàng đang làm mưa làm gió trong làng thời trang mùa hè 2026.", nullable = true)
    private String shortDescription;

    @Schema(description = "Nội dung đầy đủ bài viết — hỗ trợ HTML.", nullable = true)
    private String content;

    @Schema(description = "Tên tác giả bài viết.", example = "Nguyễn Hà Linh", nullable = true)
    private String author;

    @Schema(description = "Tên danh mục bài viết.", example = "Xu hướng thời trang", nullable = true)
    private String categoryName;

    @Schema(description = "Thời điểm bài viết được đăng/tạo.", example = "2026-06-15T09:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
