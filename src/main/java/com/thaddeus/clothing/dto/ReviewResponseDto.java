package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin đánh giá sản phẩm trả về từ hệ thống")
public class ReviewResponseDto {

    @Schema(description = "ID duy nhất của đánh giá.", example = "301", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID sản phẩm được đánh giá.", example = "42")
    private Long productId;

    @Schema(description = "Tên sản phẩm được đánh giá.", example = "Áo Polo Nam Cổ Bẻ Basic Premium")
    private String productName;

    @Schema(description = "ID người dùng đã viết đánh giá.", example = "1")
    private Long userId;

    @Schema(description = "Họ tên người dùng viết đánh giá — hiển thị công khai.", example = "Nguyễn Văn A")
    private String userFullName;

    @Schema(description = "Số sao đánh giá từ 1 (tệ nhất) đến 5 (tốt nhất).", example = "5", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(description = "Nội dung nhận xét.", example = "Sản phẩm đẹp, chất vải tốt!", nullable = true)
    private String comment;

    @Schema(description = "URLs ảnh/video minh chứng, phân cách bằng dấu phẩy.", example = "https://cdn.thaddeus.vn/reviews/img1.jpg", nullable = true)
    private String mediaUrls;

    @Schema(
            description = "Trạng thái duyệt đánh giá:\n" +
                    "- `PENDING`: Chờ Admin duyệt (chưa hiển thị công khai)\n" +
                    "- `APPROVED`: Đã duyệt, hiển thị công khai\n" +
                    "- `REJECTED`: Bị từ chối (vi phạm nội quy)",
            example = "PENDING",
            allowableValues = {"PENDING", "APPROVED", "REJECTED"}
    )
    private String status;
}
