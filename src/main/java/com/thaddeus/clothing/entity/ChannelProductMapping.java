package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(
    name = "channel_product_mappings",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sales_channel_id", "product_variant_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChannelProductMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_channel_id", nullable = false)
    private SalesChannel salesChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @NotNull(message = "Giá bán trên kênh không được trống")
    @Min(value = 0)
    @Column(name = "channel_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal channelPrice; // Giá bán riêng biệt áp dụng cho kênh này

    @Column(name = "external_product_id")
    private String externalProductId; // ID sản phẩm trên sàn (Ví dụ: Shopee Item ID)

    @Column(nullable = false)
    private String syncStatus; // SYNCED, PENDING, ERROR
}
