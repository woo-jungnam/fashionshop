package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "coupons",
    indexes = {
        @Index(name = "idx_coupon_code", columnList = "code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Mã giảm giá không được trống")
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank(message = "Loại giảm giá không được trống")
    @Column(name = "discount_type", nullable = false)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    @NotNull(message = "Giá trị giảm bắt buộc nhập")
    @Min(value = 0, message = "Giá trị giảm không được âm")
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Min(value = 0, message = "Đơn hàng tối thiểu không được âm")
    @Column(name = "min_order_value", precision = 12, scale = 2)
    private BigDecimal minOrderValue;

    @Min(value = 0, message = "Giảm tối đa không được âm")
    @Column(name = "max_discount_value", precision = 12, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @NotNull(message = "Tổng số lượng phát hành bắt buộc nhập")
    @Min(value = 0, message = "Tổng số lượng tối thiểu là 0")
    @Column(name = "total_limit", nullable = false)
    private Integer totalLimit;

    @NotNull(message = "Số lượng đã sử dụng bắt buộc nhập")
    @Min(value = 0, message = "Số lượng sử dụng tối thiểu là 0")
    @Builder.Default
    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @NotNull(message = "Giới hạn sử dụng mỗi user bắt buộc nhập")
    @Min(value = 1, message = "Giới hạn tối thiểu là 1")
    @Column(name = "user_limit", nullable = false)
    private Integer userLimit;

    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, INACTIVE
}
