package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_coupons",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @NotNull
    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    @NotNull
    @Min(value = 0)
    @Builder.Default
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0; // Số lần user đã dùng mã này (đối chiếu với userLimit của Coupon)

    @NotBlank(message = "Trạng thái mã giảm giá của User không được trống")
    @Column(nullable = false)
    private String status; // UNUSED, USED, EXPIRED
}
