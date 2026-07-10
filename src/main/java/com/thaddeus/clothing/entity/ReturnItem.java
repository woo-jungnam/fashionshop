package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "return_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReturnItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @NotNull(message = "Số lượng đổi trả bắt buộc nhập")
    @Min(value = 1, message = "Số lượng đổi trả tối thiểu là 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotBlank(message = "Tình trạng hàng thu hồi bắt buộc nhập")
    @Column(name = "condition_state", nullable = false)
    private String conditionState; // NGUYEN_TAG, DA_GIAT, HONG
}
