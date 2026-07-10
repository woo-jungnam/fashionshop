package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.CouponRequestDto;
import com.thaddeus.clothing.dto.CouponResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.CouponService;
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
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(
        name = "Coupon",
        description = """
                ## Quản lý mã giảm giá (Coupon)
                
                **Quyền truy cập:**
                - GET (tra cứu coupon): Công khai — không cần JWT
                - POST (tạo coupon): Yêu cầu JWT (Admin)
                - PUT/DELETE (vô hiệu hoá): Yêu cầu JWT (Admin)
                
                **Hai loại giảm giá:**
                - `PERCENTAGE`: Giảm theo % (ví dụ: giảm 20%, tối đa 100.000đ)
                - `FIXED_AMOUNT`: Giảm số tiền cố định (ví dụ: giảm 50.000đ)
                
                **Để người dùng thu thập và sử dụng coupon:**
                - Thu thập: `POST /api/v1/user-coupons/collect?userId=1&couponId=15`
                - Xem coupon đã thu: `GET /api/v1/user-coupons?userId=1`
                - Dùng khi đặt hàng: Truyền `couponCode` vào `POST /api/v1/orders`
                """
)
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Tạo mới mã giảm giá",
            description = """
                    Admin tạo mới một coupon giảm giá. **Yêu cầu JWT (Admin).**
                    
                    **Ví dụ tạo coupon PERCENTAGE:**
                    - `discountType: PERCENTAGE`, `discountValue: 20` → giảm 20%
                    - `maxDiscountValue: 100000` → tối đa giảm 100.000đ
                    - `minOrderValue: 200000` → đơn từ 200.000đ mới áp dụng được
                    
                    **Ví dụ tạo coupon FIXED_AMOUNT:**
                    - `discountType: FIXED_AMOUNT`, `discountValue: 50000` → giảm cố định 50.000đ
                    - `minOrderValue: 300000` → đơn từ 300.000đ mới áp dụng được
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo coupon thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponResponseDto.class),
                            examples = {
                                    @ExampleObject(name = "Coupon PERCENTAGE 20%", value = """
                                            {
                                              "id": 15,
                                              "code": "SUMMER2026",
                                              "discountType": "PERCENTAGE",
                                              "discountValue": 20,
                                              "minOrderValue": 200000,
                                              "maxDiscountValue": 100000,
                                              "startDate": "2026-07-01T00:00:00",
                                              "endDate": "2026-08-31T23:59:59",
                                              "totalLimit": 500,
                                              "usedCount": 0,
                                              "userLimit": 1,
                                              "status": "ACTIVE"
                                            }
                                            """),
                                    @ExampleObject(name = "Coupon FIXED 50k", value = """
                                            {
                                              "id": 16,
                                              "code": "NEWUSER50K",
                                              "discountType": "FIXED_AMOUNT",
                                              "discountValue": 50000,
                                              "minOrderValue": 300000,
                                              "maxDiscountValue": null,
                                              "totalLimit": 1000,
                                              "usedCount": 0,
                                              "userLimit": 1,
                                              "status": "ACTIVE"
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CouponResponseDto> createCoupon(@Valid @RequestBody CouponRequestDto request) {
        CouponResponseDto response = couponService.createCoupon(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy coupon theo ID", description = "Tra cứu thông tin coupon theo ID nội bộ. **API công khai.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy coupon",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Coupon không tồn tại hoặc đã hết hạn (CPN_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CouponResponseDto> getCouponById(
            @Parameter(description = "ID coupon", required = true, example = "15")
            @PathVariable("id") Long id) {
        CouponResponseDto response = couponService.getCouponById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @Operation(
            summary = "Tra cứu coupon theo mã code",
            description = """
                    Tìm coupon theo mã code do người dùng nhập. **API công khai.**
                    
                    Thường dùng để **kiểm tra trước khi áp dụng** — Frontend gọi API này khi người dùng nhập mã coupon
                    để hiển thị thông tin giảm giá trước khi đặt hàng.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy coupon với code đã cho",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 15,
                                      "code": "SUMMER2026",
                                      "discountType": "PERCENTAGE",
                                      "discountValue": 20,
                                      "minOrderValue": 200000,
                                      "maxDiscountValue": 100000,
                                      "endDate": "2026-08-31T23:59:59",
                                      "totalLimit": 500,
                                      "usedCount": 127,
                                      "userLimit": 1,
                                      "status": "ACTIVE"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Mã coupon không tồn tại, đã hết hạn hoặc đã hết lượt dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-07-10T12:00:00","status":400,"error":"Bad Request","errorCode":"CPN_001","message":"Mã giảm giá không tồn tại hoặc đã hết hạn","path":"/api/v1/coupons/code/WRONGCODE"}
                                    """)))
    })
    public ResponseEntity<CouponResponseDto> getCouponByCode(
            @Parameter(description = "Mã coupon cần tra cứu (phân biệt chữ hoa/thường)", required = true, example = "SUMMER2026")
            @PathVariable("code") String code) {
        CouponResponseDto response = couponService.getCouponByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách coupon (có phân trang)",
            description = "Lấy toàn bộ danh sách coupon trong hệ thống với phân trang. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách coupon (Page object)",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Page<CouponResponseDto>> getAllCoupons(
            @Parameter(hidden = true) Pageable pageable) {
        Page<CouponResponseDto> response = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Vô hiệu hoá coupon",
            description = """
                    Chuyển trạng thái coupon sang `INACTIVE` — coupon không thể dùng thêm. **Yêu cầu JWT (Admin).**
                    
                    Trả về `204 No Content`. Dùng khi cần dừng coupon trước hạn (ví dụ: bị lạm dụng).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vô hiệu hoá thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy coupon",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deactivateCoupon(
            @Parameter(description = "ID coupon cần vô hiệu hoá", required = true, example = "15")
            @PathVariable("id") Long id) {
        couponService.deactivateCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
