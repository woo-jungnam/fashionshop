package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tạo mới mã giảm giá coupon")
public class CouponRequestDto {

    @NotBlank(message = "Mã giảm giá không được trống")
    @Schema(
            description = "Mã coupon mà khách hàng nhập vào khi thanh toán. " +
                    "Phải là duy nhất trong hệ thống. Thường là chữ hoa và số, không có khoảng trắng.",
            example = "SUMMER2026",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String code;

    @NotBlank(message = "Loại giảm giá không được trống")
    @Schema(
            description = "Loại giảm giá:\n" +
                    "- `PERCENTAGE`: Giảm theo % — ví dụ: giảm 20% trên tổng đơn\n" +
                    "- `FIXED_AMOUNT`: Giảm số tiền cố định — ví dụ: giảm 50.000đ",
            example = "PERCENTAGE",
            allowableValues = {"PERCENTAGE", "FIXED_AMOUNT"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String discountType;

    @NotNull(message = "Giá trị giảm không được trống")
    @Min(value = 0, message = "Giá trị giảm không được âm")
    @Schema(
            description = "Giá trị giảm giá:\n" +
                    "- Nếu `discountType = PERCENTAGE`: nhập % giảm (ví dụ: 20 = giảm 20%)\n" +
                    "- Nếu `discountType = FIXED_AMOUNT`: nhập số tiền VND (ví dụ: 50000 = giảm 50.000đ)\n" +
                    "Không được âm.",
            example = "20",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
    )
    private BigDecimal discountValue;

    @Min(value = 0, message = "Đơn hàng tối thiểu không được âm")
    @Schema(
            description = "Giá trị đơn hàng tối thiểu (VND) để áp dụng coupon. " +
                    "Ví dụ: 200000 = đơn hàng phải từ 200.000đ mới dùng được coupon. " +
                    "Nếu không đủ điều kiện, hệ thống báo lỗi CPN_003. " +
                    "Để null hoặc 0 nếu không có điều kiện tối thiểu.",
            example = "200000",
            nullable = true,
            minimum = "0"
    )
    private BigDecimal minOrderValue;

    @Min(value = 0, message = "Giảm tối đa không được âm")
    @Schema(
            description = "Số tiền giảm tối đa (VND) — chỉ áp dụng khi `discountType = PERCENTAGE`. " +
                    "Ví dụ: discountValue = 30%, maxDiscountValue = 100000 → dù đơn 1tr thì cũng chỉ giảm tối đa 100.000đ. " +
                    "Để null nếu không giới hạn.",
            example = "100000",
            nullable = true,
            minimum = "0"
    )
    private BigDecimal maxDiscountValue;

    @Schema(
            description = "Thời điểm bắt đầu hiệu lực của coupon (ISO 8601 format: yyyy-MM-ddTHH:mm:ss). " +
                    "Null nghĩa là hiệu lực ngay từ khi tạo.",
            example = "2026-07-01T00:00:00",
            nullable = true
    )
    private LocalDateTime startDate;

    @Schema(
            description = "Thời điểm hết hạn coupon (ISO 8601 format: yyyy-MM-ddTHH:mm:ss). " +
                    "Null nghĩa là không giới hạn thời gian.",
            example = "2026-08-31T23:59:59",
            nullable = true
    )
    private LocalDateTime endDate;

    @NotNull(message = "Tổng giới hạn sử dụng không được trống")
    @Min(value = 0, message = "Tổng giới hạn sử dụng tối thiểu là 0")
    @Schema(
            description = "Tổng số lần coupon có thể được sử dụng trong toàn hệ thống. " +
                    "0 nghĩa là không giới hạn tổng. " +
                    "Ví dụ: 500 = tối đa 500 đơn hàng dùng coupon này.",
            example = "500",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
    )
    private Integer totalLimit;

    @NotNull(message = "Giới hạn sử dụng mỗi User không được trống")
    @Min(value = 1, message = "Giới hạn sử dụng mỗi User tối thiểu là 1")
    @Schema(
            description = "Số lần tối đa một user có thể dùng coupon này. " +
                    "Ví dụ: 1 = mỗi tài khoản chỉ dùng được 1 lần. " +
                    "Nếu vượt quá, hệ thống báo lỗi CPN_002.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer userLimit;

    @NotBlank(message = "Trạng thái không được trống")
    @Schema(
            description = "Trạng thái coupon:\n- `ACTIVE`: Đang hoạt động, có thể sử dụng\n- `INACTIVE`: Đã vô hiệu hóa",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;
}
