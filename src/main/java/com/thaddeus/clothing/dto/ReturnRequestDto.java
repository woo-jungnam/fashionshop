package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin yêu cầu đổi trả hàng. Chỉ được gửi trong vòng 7 ngày sau khi đơn hàng được giao thành công (DELIVERED).")
public class ReturnRequestDto {

    @NotNull(message = "Mã đơn hàng không được trống")
    @Schema(
            description = "ID đơn hàng cần đổi trả — lấy từ GET /api/v1/orders/{id}. " +
                    "Đơn hàng phải ở trạng thái DELIVERED mới được tạo yêu cầu đổi trả.",
            example = "1001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long orderId;

    @NotBlank(message = "Lý do đổi trả không được trống")
    @Schema(
            description = "Lý do đổi trả sản phẩm. Phải mô tả cụ thể để Admin xem xét. " +
                    "Ví dụ lý do hợp lệ: 'Sản phẩm bị lỗi vải, đường may bị tuột', 'Giao sai màu/size so với đơn hàng'.",
            example = "Sản phẩm bị lỗi đường may ở tay áo trái, vải bị xổ chỉ",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String reason;

    @Schema(
            description = "URLs ảnh/video bằng chứng lỗi sản phẩm, phân cách bằng dấu phẩy (tuỳ chọn nhưng khuyến khích). " +
                    "Upload ảnh lên storage trước rồi truyền URL vào đây.",
            example = "https://cdn.thaddeus.vn/returns/evidence1.jpg,https://cdn.thaddeus.vn/returns/evidence2.jpg",
            nullable = true
    )
    private String evidenceUrls;

    @NotEmpty(message = "Phải chọn ít nhất một sản phẩm đổi trả")
    @Schema(
            description = "Danh sách các sản phẩm cần đổi trả trong đơn hàng. Phải chọn ít nhất 1 sản phẩm. " +
                    "Có thể đổi trả một phần đơn hàng (không cần đổi tất cả).",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ReturnItemRequestDto> items;
}
