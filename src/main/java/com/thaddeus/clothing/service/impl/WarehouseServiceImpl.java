package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.WarehouseRequestDto;
import com.thaddeus.clothing.dto.WarehouseResponseDto;
import com.thaddeus.clothing.dto.WarehouseStockAdjustRequestDto;
import com.thaddeus.clothing.dto.WarehouseStockResponseDto;
import com.thaddeus.clothing.entity.ProductVariant;
import com.thaddeus.clothing.entity.Warehouse;
import com.thaddeus.clothing.entity.WarehouseInventory;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.ProductVariantRepository;
import com.thaddeus.clothing.repository.WarehouseInventoryRepository;
import com.thaddeus.clothing.repository.WarehouseRepository;
import com.thaddeus.clothing.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public WarehouseResponseDto createWarehouse(WarehouseRequestDto request) {
        if (warehouseRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.WAREHOUSE_ALREADY_EXISTS);
        }

        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .address(request.getAddress())
                .warehouseType(request.getWarehouseType())
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponseDto getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));
        return mapToResponseDto(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarehouseResponseDto updateWarehouse(Long id, WarehouseRequestDto request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));

        if (warehouseRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException(ErrorCode.WAREHOUSE_ALREADY_EXISTS);
        }

        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setWarehouseType(request.getWarehouseType());

        Warehouse updated = warehouseRepository.save(warehouse);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));
        warehouseRepository.delete(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseStockResponseDto> getWarehouseInventory(Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }

        return warehouseInventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToStockResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WarehouseStockResponseDto inboundStock(WarehouseStockAdjustRequestDto request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));

        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        WarehouseInventory inventory = warehouseInventoryRepository
                .findWithLock(request.getWarehouseId(), request.getProductVariantId())
                .orElse(null);

        if (inventory == null) {
            inventory = WarehouseInventory.builder()
                    .warehouse(warehouse)
                    .productVariant(variant)
                    .physicalQty(request.getQuantity())
                    .allocatedQty(0)
                    .availableToSellQty(request.getQuantity())
                    .build();
        } else {
            inventory.setPhysicalQty(inventory.getPhysicalQty() + request.getQuantity());
            inventory.setAvailableToSellQty(inventory.getAvailableToSellQty() + request.getQuantity());
        }

        WarehouseInventory saved = warehouseInventoryRepository.save(inventory);
        return mapToStockResponseDto(saved);
    }

    @Override
    @Transactional
    public WarehouseStockResponseDto outboundStock(WarehouseStockAdjustRequestDto request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));

        productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        WarehouseInventory inventory = warehouseInventoryRepository
                .findWithLock(request.getWarehouseId(), request.getProductVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

        if (inventory.getAvailableToSellQty() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        inventory.setPhysicalQty(inventory.getPhysicalQty() - request.getQuantity());
        inventory.setAvailableToSellQty(inventory.getAvailableToSellQty() - request.getQuantity());

        WarehouseInventory saved = warehouseInventoryRepository.save(inventory);
        return mapToStockResponseDto(saved);
    }

    private WarehouseResponseDto mapToResponseDto(Warehouse warehouse) {
        return WarehouseResponseDto.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .address(warehouse.getAddress())
                .warehouseType(warehouse.getWarehouseType())
                .build();
    }

    private WarehouseStockResponseDto mapToStockResponseDto(WarehouseInventory inventory) {
        return WarehouseStockResponseDto.builder()
                .id(inventory.getId())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .productVariantId(inventory.getProductVariant().getId())
                .sku(inventory.getProductVariant().getSku())
                .physicalQty(inventory.getPhysicalQty())
                .allocatedQty(inventory.getAllocatedQty())
                .availableToSellQty(inventory.getAvailableToSellQty())
                .build();
    }
}
