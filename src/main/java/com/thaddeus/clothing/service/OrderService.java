package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.OrderRequestDto;
import com.thaddeus.clothing.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto checkout(Long userId, OrderRequestDto request);
    OrderResponseDto getOrderById(Long id);
    Page<OrderResponseDto> getOrdersByUser(Long userId, Pageable pageable);
}
