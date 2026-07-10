package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin đánh giá sản phẩm. Chỉ được đánh giá sản phẩm đã mua và đơn hàng đã giao thành công.")
public class ReviewRequestDto {

    @NotNull(message = "Mã sản phẩm không được trống")
    @Schema(
            description = "ID sản phẩm muốn đánh giá — lấy từ thông tin đơn hàng.",
            example = "42",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long productId;

    @NotNull(message = "Mã dòng hàng đơn không được trống")
    @Schema(
            description = "ID dòng hàng trong đơn (orderItemId) — lấy từ OrderResponseDto.items[].id. " +
                    "Bắt buộc để hệ thống xác nhận người dùng thực sự đã mua sản phẩm này (tránh review giả).",
            example = "501",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long orderItemId;

    @NotNull(message = "Số sao đánh giá không được trống")
    @Min(value = 1, message = "Số sao tối thiểu là 1")
    @Max(value = 5, message = "Số sao tối đa là 5")
    @Schema(
            description = "Số sao đánh giá từ 1 đến 5:\n- 1 ⭐: Rất tệ\n- 2 ⭐: Tệ\n- 3 ⭐: Bình thường\n- 4 ⭐: Tốt\n- 5 ⭐: Rất tốt",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1",
            maximum = "5"
    )
    private Integer rating;

    @Schema(
            description = "Nội dung nhận xét chi tiết về sản phẩm (tuỳ chọn). " +
                    "Hỗ trợ văn bản thuần, không hỗ trợ HTML.",
            example = "Sản phẩm đẹp, chất vải tốt, đúng màu như ảnh. Giao hàng nhanh, đóng gói cẩn thận. Sẽ ủng hộ shop lần sau!",
            nullable = true
    )
    private String comment;

    @Schema(
            description = "URLs ảnh hoặc video đánh giá, phân cách nhau bằng dấu phẩy (tuỳ chọn). " +
                    "Người dùng cần upload ảnh lên storage trước, sau đó truyền URL vào đây.",
            example = "https://cdn.thaddeus.vn/reviews/img1.jpg,https://cdn.thaddeus.vn/reviews/img2.jpg",
            nullable = true
    )
    private String mediaUrls;
}
