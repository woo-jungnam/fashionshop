package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.UserCouponResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.UserCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-coupons")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "User Coupon",
        description = """
                ## Coupon của người dùng
                
                Tất cả API đều **yêu cầu JWT**.
                
                **Luồng thu thập và sử dụng coupon:**
                1. Người dùng xem danh sách coupon: `GET /api/v1/coupons`
                2. Thu thập coupon: `POST /api/v1/user-coupons/collect?userId=1&couponId=15`
                3. Xem coupon đã thu: `GET /api/v1/user-coupons?userId=1`
                4. Khi đặt hàng, dùng `code` trong `POST /api/v1/orders` với field `couponCode`
                
                **Trạng thái UserCoupon:**
                - `UNUSED`: Chưa dùng — còn có thể sử dụng khi đặt hàng
                - `USED`: Đã dùng trong một đơn hàng
                - `EXPIRED`: Coupon gốc đã hết hạn
                """
)
public class UserCouponController {

    private final UserCouponService userCouponService;

    @GetMapping
    @Operation(
            summary = "Lấy danh sách coupon của người dùng",
            description = """
                    Trả về tất cả coupon mà người dùng đã thu thập. **Yêu cầu JWT.**
                    
                    Frontend dùng API này để hiển thị "Ví coupon" của người dùng,
                    và để người dùng chọn coupon áp dụng khi thanh toán.
                    
                    Chỉ hiển thị coupon có trạng thái `UNUSED` khi người dùng chọn mã giảm giá.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách coupon của người dùng",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserCouponResponseDto.class)),
                            examples = @ExampleObject(name = "Ví coupon người dùng #1", value = """
                                    [
                                      {
                                        "id": 66,
                                        "couponId": 15,
                                        "code": "SUMMER2026",
                                        "discountType": "PERCENTAGE",
                                        "discountValue": 20,
                                        "minOrderValue": 200000,
                                        "expiryDate": "2026-08-31T23:59:59",
                                        "status": "UNUSED"
                                      },
                                      {
                                        "id": 67,
                                        "couponId": 16,
                                        "code": "NEWUSER50K",
                                        "discountType": "FIXED_AMOUNT",
                                        "discountValue": 50000,
                                        "minOrderValue": 300000,
                                        "expiryDate": null,
                                        "status": "USED"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UserCouponResponseDto>> getUserCoupons(
            @Parameter(description = "ID người dùng cần lấy danh sách coupon", required = true, example = "1")
            @RequestParam("userId") Long userId) {
        List<UserCouponResponseDto> response = userCouponService.getUserCoupons(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/collect")
    @Operation(
            summary = "Thu thập coupon",
            description = """
                    Người dùng thu thập (claim) một coupon vào ví. **Yêu cầu JWT.**
                    
                    **Kiểm tra trước khi thu thập:**
                    - Coupon phải tồn tại và đang `ACTIVE`
                    - Coupon chưa hết `totalLimit` (tổng lượt dùng)
                    - Người dùng chưa thu thập coupon này quá `userLimit` lần
                    
                    **Sau khi thu thập thành công** → Coupon xuất hiện trong `GET /api/v1/user-coupons`
                    với `status = UNUSED`.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thu thập coupon thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCouponResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 66,
                                      "couponId": 15,
                                      "code": "SUMMER2026",
                                      "discountType": "PERCENTAGE",
                                      "discountValue": 20,
                                      "minOrderValue": 200000,
                                      "expiryDate": "2026-08-31T23:59:59",
                                      "status": "UNUSED"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Coupon đã hết lượt thu thập (CPN_002) hoặc người dùng đã thu hết limit",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-07-10T12:00:00","status":400,"error":"Bad Request","errorCode":"CPN_002","message":"Mã giảm giá đã hết lượt sử dụng","path":"/api/v1/user-coupons/collect"}
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy coupon hoặc người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserCouponResponseDto> collectCoupon(
            @Parameter(description = "ID người dùng thu thập coupon", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Parameter(description = "ID coupon cần thu thập", required = true, example = "15")
            @RequestParam("couponId") Long couponId) {
        UserCouponResponseDto response = userCouponService.collectCoupon(userId, couponId);
        return ResponseEntity.ok(response);
    }
}
