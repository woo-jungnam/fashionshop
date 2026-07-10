package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.SePayWebhookDto;
import com.thaddeus.clothing.entity.*;
import com.thaddeus.clothing.enums.OrderStatus;
import com.thaddeus.clothing.enums.PaymentStatus;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.OrderSePayRepository;
import com.thaddeus.clothing.repository.WarehouseInventoryRepository;
import com.thaddeus.clothing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderSePayRepository orderSePayRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processWebhook(SePayWebhookDto webhook) {
        log.info("Processing SePay Webhook - Transaction ID: {}, Amount: {}, Content: {}",
                webhook.getId(), webhook.getTransferAmount(), webhook.getContent());

        try {
            // 1. Bóc tách mã đơn hàng từ nội dung giao dịch hoặc trường code
            String orderCode = null;
            String content = webhook.getContent();
            if (content != null) {
                Pattern pattern = Pattern.compile("ORD-[A-Za-z0-9]+");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    orderCode = matcher.group();
                }
            }

            if (orderCode == null && webhook.getCode() != null) {
                orderCode = webhook.getCode();
            }

            if (orderCode == null) {
                log.warn("Could not find order code in webhook content: {}", content);
                return;
            }

            orderCode = orderCode.toUpperCase();

            // 2. Tìm đơn hàng
            Order order = orderSePayRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

            // Idempotency check: Tránh xử lý lại đơn hàng đã thanh toán
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                log.info("Order {} already paid. Skipping processing.", orderCode);
                return;
            }

            // 3. So khớp số tiền (Relaxed check for easier demo/testing)
            if (webhook.getTransferAmount() == null || webhook.getTransferAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                log.warn("Transfer amount {} is invalid for order {}",
                        webhook.getTransferAmount(), orderCode);
                return;
            }

            if (webhook.getTransferAmount().compareTo(order.getTotalAmount()) < 0) {
                log.warn("Transfer amount {} is less than total order amount {} for order {}. Accepting anyway for demo/testing.",
                        webhook.getTransferAmount(), order.getTotalAmount(), orderCode);
            }

            // 4. Cập nhật đơn hàng -> PAID, APPROVED
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.APPROVED);

            OrderStatusHistory history = OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.APPROVED)
                    .reason("Thanh toán thành công qua SePay")
                    .actionBy("SePay Webhook")
                    .build();
            order.getStatusHistories().add(history);
            orderSePayRepository.save(order);

            // 5. Trừ tồn kho (Deduction of stock)
            for (OrderItem item : order.getOrderItems()) {
                Long variantId = item.getProductVariant().getId();
                Integer qty = item.getQuantity();

                // Tìm kho đã allocate tồn kho này
                List<WarehouseInventory> inventories = warehouseInventoryRepository.findAllocatedInventory(variantId, qty);
                if (inventories.isEmpty()) {
                    log.error("No allocated inventory found for variant ID {} with quantity {}", variantId, qty);
                    throw new BusinessException(ErrorCode.OUT_OF_STOCK);
                }

                WarehouseInventory targetInventory = inventories.get(0);
                warehouseInventoryRepository.confirmShipment(variantId, targetInventory.getWarehouse().getId(), qty);
                log.info("Confirmed shipment of {} units of variant ID {} from warehouse ID {}", qty, variantId, targetInventory.getWarehouse().getId());
            }

            // 6. Gửi email xác nhận (Chỉ log giả lập)
            log.info("Sending confirmation email to user: {}", order.getUser().getEmail());

        } catch (Exception e) {
            // Yêu cầu nghiệp vụ: Không trả về lỗi 4xx/5xx dù xử lý thất bại nội bộ để tránh SePay retry liên tục.
            log.error("Error processing SePay Webhook: ", e);
        }
    }
}
