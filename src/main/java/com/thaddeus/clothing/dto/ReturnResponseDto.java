package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin yêu cầu đổi trả hàng trả về từ hệ thống")
public class ReturnResponseDto {

    @Schema(description = "ID duy nhất của yêu cầu đổi trả.", example = "88", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID đơn hàng liên quan.", example = "1001")
    private Long orderId;

    @Schema(description = "Lý do đổi trả do khách hàng ghi.", example = "Sản phẩm bị lỗi đường may ở tay áo trái")
    private String reason;

    @Schema(description = "URLs ảnh/video bằng chứng.", example = "https://cdn.thaddeus.vn/returns/evidence1.jpg", nullable = true)
    private String evidenceUrls;

    @Schema(
            description = "Trạng thái xử lý yêu cầu đổi trả:\n" +
                    "- `PENDING`: Chờ Admin xem xét\n" +
                    "- `APPROVED`: Đã duyệt, đang xử lý hoàn hàng/hoàn tiền\n" +
                    "- `REJECTED`: Bị từ chối (không đủ điều kiện đổi trả)\n" +
                    "- `COMPLETED`: Đã hoàn tất đổi trả",
            example = "PENDING",
            allowableValues = {"PENDING", "APPROVED", "REJECTED", "COMPLETED"}
    )
    private String status;

    @Schema(description = "Danh sách các sản phẩm trong yêu cầu đổi trả.")
    private List<ReturnItemResponseDto> items;
}
