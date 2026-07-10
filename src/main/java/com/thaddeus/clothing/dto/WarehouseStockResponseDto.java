package com.thaddeus.clothing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStockResponseDto {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long productVariantId;
    private String sku;
    private Integer physicalQty;
    private Integer allocatedQty;
    private Integer availableToSellQty;
}
