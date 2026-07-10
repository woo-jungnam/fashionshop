package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.*;
import com.thaddeus.clothing.entity.*;
import com.thaddeus.clothing.enums.OrderStatus;
import com.thaddeus.clothing.enums.PaymentStatus;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.*;
import com.thaddeus.clothing.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ShipperRepository shipperRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDto checkout(Long userId, OrderRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));

        Shipper shipper = shipperRepository.findById(request.getShipperId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPPER_NOT_FOUND));

        Order order = Order.builder()
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .shippingFee(BigDecimal.valueOf(30000)) // Phí mặc định
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemDto.getProductVariantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

            WarehouseInventory inventory = warehouseInventoryRepository
                    .findWithLock(warehouse.getId(), variant.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.OUT_OF_STOCK));

            if (inventory.getAvailableToSellQty() < itemDto.getQuantity()) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK);
            }

            inventory.setAllocatedQty(inventory.getAllocatedQty() + itemDto.getQuantity());
            inventory.setAvailableToSellQty(inventory.getAvailableToSellQty() - itemDto.getQuantity());
            warehouseInventoryRepository.save(inventory);

            BigDecimal itemPrice = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
            subTotal = subTotal.add(itemPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .quantity(itemDto.getQuantity())
                    .priceAtPurchase(itemPrice)
                    .build();

            order.addOrderItem(orderItem);
        }

        BigDecimal total = subTotal.add(order.getShippingFee());

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findActiveCoupon(request.getCouponCode())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

            if (coupon.getMinOrderValue() != null && subTotal.compareTo(coupon.getMinOrderValue()) < 0) {
                throw new BusinessException(ErrorCode.COUPON_MIN_ORDER_NOT_MET);
            }

            BigDecimal discount = BigDecimal.ZERO;
            if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                discount = subTotal.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
                if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
                    discount = coupon.getMaxDiscountValue();
                }
            } else if ("FIXED_AMOUNT".equals(coupon.getDiscountType())) {
                discount = coupon.getDiscountValue();
            }

            if (discount.compareTo(subTotal) > 0) {
                discount = subTotal;
            }

            order.setDiscountAmount(discount);
            order.setCoupon(coupon);
            total = total.subtract(discount);

            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        order.setTotalAmount(total);

        ShippingInfo shippingInfo = ShippingInfo.builder()
                .order(order)
                .shipper(shipper)
                .shippingAddress(request.getShippingAddress())
                .shippingFee(order.getShippingFee())
                .build();
        order.setShippingInfo(shippingInfo);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.PENDING)
                .reason("Khách đặt hàng thành công")
                .actionBy(user.getFullName())
                .build();
        order.getStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return mapToResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToResponseDto);
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
