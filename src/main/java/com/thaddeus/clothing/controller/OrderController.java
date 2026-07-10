package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.OrderRequestDto;
import com.thaddeus.clothing.dto.OrderResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Order",
        description = """
                ## Quản lý đơn hàng
                
                Tất cả API đơn hàng đều **yêu cầu JWT**.
                
                **Luồng đặt hàng đầy đủ:**
                1. Người dùng thêm sản phẩm vào giỏ (`POST /api/v1/carts/items`)
                2. Checkout: `POST /api/v1/orders?userId=1` → nhận `orderCode` (ví dụ: `ORD-A1B2C3`)
                3. Thanh toán: Chuyển khoản ngân hàng với nội dung chứa `orderCode`
                4. SePay tự động xác nhận → `paymentStatus` chuyển sang `PAID`, `status` chuyển sang `APPROVED`
                
                **Enum OrderStatus:**
                - `PENDING` → `APPROVED` → `SHIPPING` → `DELIVERED`
                - Hoặc: `PENDING/APPROVED` → `CANCELLED`
                - Hoặc: `DELIVERED` → `RETURNED` → `REFUNDED`
                
                **Enum PaymentStatus:**
                - `UNPAID` → `PAID` (sau khi SePay xác nhận) hoặc `CANCELLED`
                """
)
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Đặt hàng (Checkout)",
            description = """
                    Tạo mới một đơn hàng từ danh sách sản phẩm. **Yêu cầu JWT.**
                    
                    **Quy trình backend sau khi nhận request:**
                    1. Validate tất cả `productVariantId` tồn tại và đang ACTIVE
                    2. Kiểm tra tồn kho tại `warehouseId` cho từng sản phẩm
                    3. Kiểm tra `couponCode` hợp lệ và đủ điều kiện (nếu có)
                    4. Tính `totalAmount = Σ(price × quantity) - discount + shippingFee`
                    5. Tạo đơn hàng với `status = PENDING`, `paymentStatus = UNPAID`
                    6. Sinh `orderCode` dạng `ORD-XXXXXX` (dùng trong nội dung chuyển khoản)
                    7. **Không trừ tồn kho ngay** — chỉ trừ sau khi thanh toán xác nhận
                    
                    **Sau khi nhận orderCode:**
                    - Hiển thị thông tin chuyển khoản cho người dùng
                    - Nội dung chuyển khoản: `Thanh toan ORD-A1B2C3`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Đặt hàng thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class),
                            examples = @ExampleObject(name = "Đơn hàng vừa tạo", value = """
                                    {
                                      "id": 1001,
                                      "orderCode": "ORD-A1B2C3",
                                      "status": "PENDING",
                                      "paymentStatus": "UNPAID",
                                      "totalAmount": 548000,
                                      "discountAmount": 50000,
                                      "shippingFee": 30000,
                                      "items": [
                                        {
                                          "id": 501,
                                          "productVariantId": 101,
                                          "sku": "POLO-BASIC-PREM-001-RED-M",
                                          "productName": "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M",
                                          "quantity": 2,
                                          "priceAtPurchase": 249000
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Lỗi nghiệp vụ: hết hàng (INV_001), coupon không hợp lệ (CPN_001/CPN_002/CPN_003)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "Hết hàng", value = """
                                            {"timestamp":"2026-07-10T12:00:00","status":400,"error":"Bad Request","errorCode":"INV_001","message":"Sản phẩm đã hết hàng hoặc số lượng tồn kho khả dụng không đủ","path":"/api/v1/orders"}
                                            """),
                                    @ExampleObject(name = "Coupon không đủ điều kiện", value = """
                                            {"timestamp":"2026-07-10T12:00:00","status":400,"error":"Bad Request","errorCode":"CPN_003","message":"Giá trị đơn hàng chưa đạt hạn mức tối thiểu để áp dụng coupon","path":"/api/v1/orders"}
                                            """)
                            })),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user, warehouse, shipper hoặc variant",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderResponseDto> checkout(
            @Parameter(description = "ID người dùng đặt hàng", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Valid @RequestBody OrderRequestDto request) {
        OrderResponseDto response = orderService.checkout(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin đơn hàng theo ID",
            description = "Trả về chi tiết đơn hàng theo ID nội bộ. **Yêu cầu JWT.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy đơn hàng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng (ORD_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-07-10T12:00:00","status":404,"error":"Not Found","errorCode":"ORD_001","message":"Không tìm thấy đơn hàng","path":"/api/v1/orders/9999"}
                                    """)))
    })
    public ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(description = "ID đơn hàng (số nguyên)", required = true, example = "1001")
            @PathVariable("id") Long id) {
        OrderResponseDto response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Lấy lịch sử đơn hàng của người dùng",
            description = """
                    Trả về danh sách đơn hàng của một người dùng theo userId, có phân trang. **Yêu cầu JWT.**
                    
                    **Ví dụ URLs:**
                    - `GET /api/v1/orders/user/1?page=0&size=10` — 10 đơn gần nhất
                    - `GET /api/v1/orders/user/1?page=0&size=10&sort=id,desc` — Đơn mới nhất trước
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách đơn hàng (phân trang)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByUser(
            @Parameter(description = "ID người dùng cần lấy lịch sử đơn hàng", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<OrderResponseDto> response = orderService.getOrdersByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
