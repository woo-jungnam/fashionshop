package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin đơn hàng trả về sau khi tạo hoặc tra cứu")
public class OrderResponseDto {

    @Schema(description = "ID duy nhất của đơn hàng trong DB.", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(
            description = "Mã đơn hàng định dạng ORD-XXXXXX — dùng trong nội dung chuyển khoản ngân hàng để đối soát thanh toán qua SePay. " +
                    "Ví dụ: khi chuyển khoản, ghi nội dung 'Thanh toan ORD-A1B2C3'.",
            example = "ORD-A1B2C3",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String orderCode;

    @Schema(
            description = "Trạng thái hiện tại của đơn hàng:\n" +
                    "- `PENDING`: Chờ xác nhận (vừa tạo)\n" +
                    "- `APPROVED`: Đã xác nhận / đã thanh toán\n" +
                    "- `SHIPPING`: Đang vận chuyển\n" +
                    "- `DELIVERED`: Đã giao hàng thành công\n" +
                    "- `CANCELLED`: Đã hủy\n" +
                    "- `RETURNED`: Đang hoàn hàng\n" +
                    "- `REFUNDED`: Đã hoàn tiền",
            example = "PENDING",
            allowableValues = {"PENDING", "APPROVED", "SHIPPING", "DELIVERED", "CANCELLED", "RETURNED", "REFUNDED"}
    )
    private String status;

    @Schema(
            description = "Trạng thái thanh toán:\n" +
                    "- `UNPAID`: Chưa thanh toán\n" +
                    "- `PAID`: Đã thanh toán\n" +
                    "- `FAILED`: Thanh toán thất bại\n" +
                    "- `CANCELLED`: Thanh toán bị hủy\n" +
                    "- `REFUNDED`: Đã hoàn tiền",
            example = "UNPAID",
            allowableValues = {"UNPAID", "PAID", "FAILED", "CANCELLED", "REFUNDED"}
    )
    private String paymentStatus;

    @Schema(description = "Tổng tiền phải thanh toán sau khi đã trừ giảm giá và cộng phí vận chuyển (đơn vị: VND).", example = "548000")
    private BigDecimal totalAmount;

    @Schema(description = "Số tiền được giảm giá từ coupon (đơn vị: VND). 0 nếu không dùng coupon.", example = "50000")
    private BigDecimal discountAmount;

    @Schema(description = "Phí vận chuyển (đơn vị: VND).", example = "30000")
    private BigDecimal shippingFee;

    @Schema(description = "Danh sách các sản phẩm trong đơn hàng.")
    private List<OrderItemResponseDto> items;
}
