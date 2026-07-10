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
@Schema(description = "Thông tin coupon giảm giá trả về từ hệ thống")
public class CouponResponseDto {

    @Schema(description = "ID duy nhất của coupon.", example = "15", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Mã coupon mà khách hàng nhập khi thanh toán.", example = "SUMMER2026")
    private String code;

    @Schema(description = "Loại giảm giá: PERCENTAGE (%) hoặc FIXED_AMOUNT (số tiền cố định VND).", example = "PERCENTAGE", allowableValues = {"PERCENTAGE", "FIXED_AMOUNT"})
    private String discountType;

    @Schema(description = "Giá trị giảm — % nếu PERCENTAGE, số tiền VND nếu FIXED_AMOUNT.", example = "20")
    private BigDecimal discountValue;

    @Schema(description = "Giá trị đơn hàng tối thiểu để áp dụng coupon (VND). Null = không có điều kiện tối thiểu.", example = "200000", nullable = true)
    private BigDecimal minOrderValue;

    @Schema(description = "Số tiền giảm tối đa (VND) — áp dụng khi loại PERCENTAGE. Null = không giới hạn.", example = "100000", nullable = true)
    private BigDecimal maxDiscountValue;

    @Schema(description = "Thời điểm bắt đầu hiệu lực.", example = "2026-07-01T00:00:00", nullable = true)
    private LocalDateTime startDate;

    @Schema(description = "Thời điểm hết hạn coupon.", example = "2026-08-31T23:59:59", nullable = true)
    private LocalDateTime endDate;

    @Schema(description = "Tổng số lần coupon được phép sử dụng. 0 = không giới hạn.", example = "500")
    private Integer totalLimit;

    @Schema(description = "Số lần coupon đã được sử dụng trong toàn hệ thống.", example = "127", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer usedCount;

    @Schema(description = "Số lần tối đa một user có thể dùng coupon này.", example = "1")
    private Integer userLimit;

    @Schema(description = "Trạng thái coupon: ACTIVE = đang hoạt động, INACTIVE = đã vô hiệu hóa.", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
