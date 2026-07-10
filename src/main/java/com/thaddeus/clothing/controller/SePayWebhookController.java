package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.SePayWebhookDto;
import com.thaddeus.clothing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(
        name = "Payment Webhook",
        description = """
                ## Xử lý Webhook Thanh toán (SePay)
                
                **Endpoint nội bộ**
                
                Endpoint này được SePay gọi tự động khi hệ thống phát hiện giao dịch ngân hàng mới khớp
                với tài khoản cấu hình trong SePay dashboard.
                
                **Cơ chế hoạt động:**
                1. Người dùng chuyển khoản với nội dung: `Thanh toan ORD-A1B2C3`
                2. SePay phát hiện giao dịch → gửi POST request vào endpoint này
                3. Backend đọc `content` từ webhook → tìm chuỗi `ORD-XXXXXX` bằng regex
                4. Tìm đơn hàng có `orderCode = ORD-A1B2C3`
                5. Kiểm tra `transferAmount >= order.totalAmount`
                6. Nếu khớp → cập nhật `paymentStatus = PAID`, `status = APPROVED`
                7. Trừ tồn kho, gửi email xác nhận
                
                **Bảo mật:** Endpoint ở path `/api/v1/payment/sepay/**` được whitelist trong SecurityConfig
                (không yêu cầu JWT vì SePay gọi từ server của họ, không có token người dùng).
                Khuyến nghị thêm `X-Sepay-Token` header để xác thực nguồn gốc request.
                """
)
public class SePayWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/sepay/webhook")
    @Operation(
            summary = "Nhận webhook thanh toán từ SePay",
            description = """
                    Endpoint nhận thông báo giao dịch từ SePay. **Tự động gọi bởi SePay — không dùng thủ công.**
                    
                    **Flow xử lý:**
                    1. Nhận `content` từ body webhook
                    2. Parse regex tìm `ORD-XXXXXX` trong `content`
                    3. So khớp `transferAmount` với `totalAmount` của đơn hàng
                    4. Nếu đủ tiền: cập nhật đơn hàng → `paymentStatus = PAID`
                    5. Trừ tồn kho và gửi email xác nhận cho khách
                    6. Trả về `200 OK` để SePay biết đã xử lý thành công
                    
                    **Không trả về lỗi 4xx/5xx** dù xử lý thất bại nội bộ — vì SePay sẽ retry nếu không nhận 200.
                    Ghi log lỗi vào hệ thống thay vì trả về lỗi HTTP.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Webhook nhận và xử lý thành công",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SePay gửi giao dịch chuyển khoản",
                                    summary = "Payload mẫu SePay gửi vào khi người dùng chuyển khoản",
                                    value = """
                                            {
                                              "id": 123456789,
                                              "gateway": "VietcomBank",
                                              "transactionDate": "10/07/2026 14:30:00",
                                              "accountNumber": "1234567890",
                                              "subAccount": null,
                                              "transferAmount": 548000,
                                              "transferType": "in",
                                              "code": "ORD-A1B2C3",
                                              "content": "Thanh toan don hang ORD-A1B2C3",
                                              "referenceCode": "FT26191ABC1234"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<String> handleSePayWebhook(@RequestBody SePayWebhookDto webhook) {
        paymentService.processWebhook(webhook);
        return ResponseEntity.ok("{\"success\": true}");
    }
}
