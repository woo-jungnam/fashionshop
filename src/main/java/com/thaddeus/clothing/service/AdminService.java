package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.DashboardMetricsResponseDto;
import com.thaddeus.clothing.dto.StockUpdateRequestDto;
import com.thaddeus.clothing.dto.OrderResponseDto;

import java.time.LocalDateTime;

public interface AdminService {
    DashboardMetricsResponseDto getDashboardMetrics(LocalDateTime start, LocalDateTime end);
    void updateWarehouseStock(StockUpdateRequestDto request);
    OrderResponseDto updateOrderStatus(Long orderId, String status, String reason, String actionBy);
}
