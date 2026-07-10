package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin một sản phẩm trong yêu cầu đổi trả")
public class ReturnItemRequestDto {

    @NotNull(message = "Mã dòng hàng đơn không được trống")
    @Schema(
            description = "ID dòng hàng trong đơn (orderItemId) — lấy từ OrderResponseDto.items[].id. " +
                    "Dùng để xác định chính xác sản phẩm nào trong đơn cần đổi trả.",
            example = "501",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long orderItemId;

    @NotNull(message = "Số lượng đổi trả không được trống")
    @Min(value = 1, message = "Số lượng đổi trả tối thiểu là 1")
    @Schema(
            description = "Số lượng sản phẩm muốn đổi trả. Tối thiểu là 1. " +
                    "Không được vượt quá số lượng đã mua trong đơn hàng.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer quantity;

    @NotBlank(message = "Tình trạng hàng đổi trả không được trống")
    @Schema(
            description = "Tình trạng hàng hoá khi trả lại:\n" +
                    "- `NGUYEN_TAG`: Còn nguyên tag, chưa qua sử dụng — điều kiện tốt nhất để đổi trả\n" +
                    "- `DA_GIAT`: Đã qua giặt nhưng chưa mặc — vẫn được xem xét\n" +
                    "- `HONG`: Sản phẩm bị hỏng, lỗi nhà sản xuất",
            example = "NGUYEN_TAG",
            allowableValues = {"NGUYEN_TAG", "DA_GIAT", "HONG"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String conditionState;
}
