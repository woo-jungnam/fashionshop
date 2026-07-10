package com.thaddeus.clothing.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelProductResponseDto {
    private Long id;
    private Long salesChannelId;
    private String salesChannelName;
    private Long productVariantId;
    private String sku;
    private BigDecimal channelPrice;
    private String externalProductId;
    private String syncStatus;
}
