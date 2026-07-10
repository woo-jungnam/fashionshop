package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.ChannelProductResponseDto;
import com.thaddeus.clothing.dto.WarehouseStockResponseDto;
import com.thaddeus.clothing.entity.ChannelProductMapping;
import com.thaddeus.clothing.entity.ProductVariant;
import com.thaddeus.clothing.entity.SalesChannel;
import com.thaddeus.clothing.entity.WarehouseInventory;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.ChannelProductMappingRepository;
import com.thaddeus.clothing.repository.ProductVariantRepository;
import com.thaddeus.clothing.repository.SalesChannelRepository;
import com.thaddeus.clothing.repository.WarehouseStockRepository;
import com.thaddeus.clothing.service.OmnichannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OmnichannelServiceImpl implements OmnichannelService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final ChannelProductMappingRepository channelProductMappingRepository;
    private final ProductVariantRepository productVariantRepository;
    private final SalesChannelRepository salesChannelRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseStockResponseDto> getStockReport(Long productVariantId) {
        List<WarehouseInventory> stocks = warehouseStockRepository.findByProductVariantId(productVariantId);
        return stocks.stream()
                .map(st -> WarehouseStockResponseDto.builder()
                        .id(st.getId())
                        .warehouseId(st.getWarehouse().getId())
                        .warehouseName(st.getWarehouse().getName())
                        .productVariantId(st.getProductVariant().getId())
                        .sku(st.getProductVariant().getSku())
                        .physicalQty(st.getPhysicalQty())
                        .allocatedQty(st.getAllocatedQty())
                        .availableToSellQty(st.getAvailableToSellQty())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChannelProductResponseDto syncProductToChannel(Long variantId, Long channelId, BigDecimal price, String externalId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        SalesChannel channel = salesChannelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        Optional<ChannelProductMapping> optional = channelProductMappingRepository
                .findBySalesChannelIdAndProductVariantId(channelId, variantId);

        ChannelProductMapping mapping;
        if (optional.isPresent()) {
            mapping = optional.get();
            mapping.setChannelPrice(price);
            mapping.setExternalProductId(externalId);
            mapping.setSyncStatus("SYNCED");
        } else {
            mapping = ChannelProductMapping.builder()
                    .salesChannel(channel)
                    .productVariant(variant)
                    .channelPrice(price)
                    .externalProductId(externalId)
                    .syncStatus("SYNCED")
                    .build();
        }

        ChannelProductMapping saved = channelProductMappingRepository.save(mapping);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelProductResponseDto> getChannelMappings(Long channelId) {
        return channelProductMappingRepository.findBySalesChannelId(channelId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ChannelProductResponseDto mapToResponseDto(ChannelProductMapping mapping) {
        return ChannelProductResponseDto.builder()
                .id(mapping.getId())
                .salesChannelId(mapping.getSalesChannel().getId())
                .salesChannelName(mapping.getSalesChannel().getName())
                .productVariantId(mapping.getProductVariant().getId())
                .sku(mapping.getProductVariant().getSku())
                .channelPrice(mapping.getChannelPrice())
                .externalProductId(mapping.getExternalProductId())
                .syncStatus(mapping.getSyncStatus())
                .build();
    }
}
