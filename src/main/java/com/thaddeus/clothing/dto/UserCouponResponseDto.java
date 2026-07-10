package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin coupon đã thu thập của người dùng")
public class UserCouponResponseDto {

    @Schema(description = "ID bản ghi coupon của người dùng.", example = "66", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID coupon gốc.", example = "15")
    private Long couponId;

    @Schema(description = "Mã coupon — nhập vào khi thanh toán đơn hàng.", example = "SUMMER2026")
    private String code;

    @Schema(description = "Loại giảm giá: PERCENTAGE hoặc FIXED_AMOUNT.", example = "PERCENTAGE", allowableValues = {"PERCENTAGE", "FIXED_AMOUNT"})
    private String discountType;

    @Schema(description = "Giá trị giảm (% hoặc VND tuỳ discountType).", example = "20")
    private java.math.BigDecimal discountValue;

    @Schema(description = "Giá trị đơn hàng tối thiểu để dùng coupon (VND).", example = "200000", nullable = true)
    private java.math.BigDecimal minOrderValue;

    @Schema(description = "Thời điểm coupon hết hạn.", example = "2026-08-31T23:59:59", nullable = true)
    private LocalDateTime expiryDate;

    @Schema(
            description = "Trạng thái coupon của người dùng:\n" +
                    "- `UNUSED`: Chưa sử dụng — còn dùng được\n" +
                    "- `USED`: Đã sử dụng trong một đơn hàng\n" +
                    "- `EXPIRED`: Đã hết hạn",
            example = "UNUSED",
            allowableValues = {"UNUSED", "USED", "EXPIRED"}
    )
    private String status;
}
