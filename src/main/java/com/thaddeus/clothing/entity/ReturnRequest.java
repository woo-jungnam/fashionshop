package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "return_requests",
    indexes = {
        @Index(name = "idx_return_request_order", columnList = "order_id"),
        @Index(name = "idx_return_request_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReturnRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotBlank(message = "Lý do đổi trả không được trống")
    @Column(nullable = false)
    private String reason;

    @Column(name = "evidence_urls", columnDefinition = "TEXT")
    private String evidenceUrls;

    @NotBlank(message = "Trạng thái yêu cầu không được trống")
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, RECEIVED, COMPLETED

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReturnItem> returnItems = new ArrayList<>();

    @OneToOne(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RefundTransaction refundTransaction;

    public void addReturnItem(ReturnItem item) {
        returnItems.add(item);
        item.setReturnRequest(this);
    }
}
