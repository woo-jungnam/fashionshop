package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.ReturnItemRequestDto;
import com.thaddeus.clothing.dto.ReturnItemResponseDto;
import com.thaddeus.clothing.dto.ReturnRequestDto;
import com.thaddeus.clothing.dto.ReturnResponseDto;
import com.thaddeus.clothing.entity.*;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.OrderItemRepository;
import com.thaddeus.clothing.repository.OrderRepository;
import com.thaddeus.clothing.repository.ReturnRequestRepository;
import com.thaddeus.clothing.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public ReturnResponseDto createReturnRequest(Long userId, ReturnRequestDto request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        ReturnRequest returnRequest = ReturnRequest.builder()
                .order(order)
                .reason(request.getReason())
                .evidenceUrls(request.getEvidenceUrls())
                .status("PENDING")
                .build();

        BigDecimal refundTotal = BigDecimal.ZERO;

        for (ReturnItemRequestDto itemDto : request.getItems()) {
            OrderItem orderItem = orderItemRepository.findById(itemDto.getOrderItemId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

            ReturnItem returnItem = ReturnItem.builder()
                    .returnRequest(returnRequest)
                    .orderItem(orderItem)
                    .quantity(itemDto.getQuantity())
                    .conditionState(itemDto.getConditionState())
                    .build();

            refundTotal = refundTotal.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            returnRequest.addReturnItem(returnItem);
        }

        RefundTransaction refundTransaction = RefundTransaction.builder()
                .returnRequest(returnRequest)
                .amount(refundTotal)
                .refundMethod("BANK_TRANSFER")
                .status("PROCESSING")
                .build();

        returnRequest.setRefundTransaction(refundTransaction);

        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponseDto getReturnById(Long id) {
        ReturnRequest request = returnRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return mapToResponseDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnResponseDto> getReturnsByUser(Long userId, Pageable pageable) {
        return returnRequestRepository.findByOrderUserId(userId, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public ReturnResponseDto updateReturnStatus(Long id, String status) {
        ReturnRequest request = returnRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        request.setStatus(status.toUpperCase());
        if ("APPROVED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
            if (request.getRefundTransaction() != null) {
                request.getRefundTransaction().setStatus("SUCCESS");
            }
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            if (request.getRefundTransaction() != null) {
                request.getRefundTransaction().setStatus("FAILED");
            }
        }

        ReturnRequest saved = returnRequestRepository.save(request);
        return mapToResponseDto(saved);
    }

    private ReturnResponseDto mapToResponseDto(ReturnRequest request) {
        List<ReturnItemResponseDto> items = request.getReturnItems().stream()
                .map(item -> ReturnItemResponseDto.builder()
                        .id(item.getId())
                        .orderItemId(item.getOrderItem().getId())
                        .productName(item.getOrderItem().getProductVariant().getProduct().getName())
                        .sku(item.getOrderItem().getProductVariant().getSku())
                        .quantity(item.getQuantity())
                        .conditionState(item.getConditionState())
                        .build())
                .collect(Collectors.toList());

        return ReturnResponseDto.builder()
                .id(request.getId())
                .orderId(request.getOrder().getId())
                .reason(request.getReason())
                .evidenceUrls(request.getEvidenceUrls())
                .status(request.getStatus())
                .items(items)
                .build();
    }
}
