package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "refund_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefundTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @NotNull(message = "Số tiền hoàn bắt buộc nhập")
    @Min(value = 0, message = "Số tiền hoàn không được âm")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Phương thức hoàn tiền không được trống")
    @Column(name = "refund_method", nullable = false)
    private String refundMethod; // BANK_TRANSFER, WALLET, CASH

    @Column(name = "transaction_code")
    private String transactionCode; // Mã giao dịch đối soát ngân hàng/ví

    @NotBlank(message = "Trạng thái giao dịch hoàn tiền không được trống")
    @Column(nullable = false)
    private String status; // SUCCESS, FAILED, PROCESSING
}
