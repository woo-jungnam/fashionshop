package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.ChannelProductResponseDto;
import com.thaddeus.clothing.dto.WarehouseStockResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface OmnichannelService {
    List<WarehouseStockResponseDto> getStockReport(Long productVariantId);
    ChannelProductResponseDto syncProductToChannel(Long variantId, Long channelId, BigDecimal price, String externalId);
    List<ChannelProductResponseDto> getChannelMappings(Long channelId);
}
