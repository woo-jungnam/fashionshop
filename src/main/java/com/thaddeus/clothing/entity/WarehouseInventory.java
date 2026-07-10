package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "warehouse_inventories",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"warehouse_id", "product_variant_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WarehouseInventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @NotNull
    @Min(0)
    @Column(name = "physical_qty", nullable = false)
    private Integer physicalQty;

    @NotNull
    @Min(0)
    @Column(name = "allocated_qty", nullable = false)
    private Integer allocatedQty;

    @NotNull
    @Min(0)
    @Column(name = "available_to_sell_qty", nullable = false)
    private Integer availableToSellQty;
}
