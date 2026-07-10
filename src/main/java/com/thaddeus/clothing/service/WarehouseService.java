package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.WarehouseRequestDto;
import com.thaddeus.clothing.dto.WarehouseResponseDto;
import com.thaddeus.clothing.dto.WarehouseStockAdjustRequestDto;
import com.thaddeus.clothing.dto.WarehouseStockResponseDto;

import java.util.List;

public interface WarehouseService {
    WarehouseResponseDto createWarehouse(WarehouseRequestDto request);
    WarehouseResponseDto getWarehouseById(Long id);
    List<WarehouseResponseDto> getAllWarehouses();
    WarehouseResponseDto updateWarehouse(Long id, WarehouseRequestDto request);
    void deleteWarehouse(Long id);

    List<WarehouseStockResponseDto> getWarehouseInventory(Long warehouseId);
    WarehouseStockResponseDto inboundStock(WarehouseStockAdjustRequestDto request);
    WarehouseStockResponseDto outboundStock(WarehouseStockAdjustRequestDto request);
}
