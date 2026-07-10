package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.DashboardMetricsResponseDto;
import com.thaddeus.clothing.dto.OrderItemResponseDto;
import com.thaddeus.clothing.dto.OrderResponseDto;
import com.thaddeus.clothing.dto.StockUpdateRequestDto;
import com.thaddeus.clothing.entity.*;
import com.thaddeus.clothing.enums.OrderStatus;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.OrderRepository;
import com.thaddeus.clothing.repository.WarehouseInventoryRepository;
import com.thaddeus.clothing.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final OrderRepository orderRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsResponseDto getDashboardMetrics(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAll(); // Trong thực tế sẽ filter theo start và end

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(o -> o.getTotalAmount())
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        long totalCount = orders.size();
        long successfulCount = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelledCount = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        long productsSold = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .flatMap(o -> o.getOrderItems().stream())
                .mapToLong(item -> item.getQuantity())
                .sum();

        return DashboardMetricsResponseDto.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalCount)
                .successfulOrders(successfulCount)
                .cancelledOrders(cancelledCount)
                .totalProductsSold(productsSold)
                .build();
    }

    @Override
    @Transactional
    public void updateWarehouseStock(StockUpdateRequestDto request) {
        WarehouseInventory inventory = warehouseInventoryRepository
                .findWithLock(request.getWarehouseId(), request.getProductVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

        // Nhập thêm hàng vào kho
        inventory.setPhysicalQty(inventory.getPhysicalQty() + request.getQuantity());
        inventory.setAvailableToSellQty(inventory.getAvailableToSellQty() + request.getQuantity());

        warehouseInventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, String status, String reason, String actionBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .reason(reason)
                .actionBy(actionBy)
                .build();
        order.getStatusHistories().add(history);

        Order saved = orderRepository.save(order);
        return mapToResponseDto(saved);
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .id(item.getId())
                        .productVariantId(item.getProductVariant().getId())
                        .sku(item.getProductVariant().getSku())
                        .productName(item.getProductVariant().getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getShippingFee())
                .items(items)
                .build();
    }
}
