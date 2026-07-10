package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.DashboardMetricsResponseDto;
import com.thaddeus.clothing.dto.StockUpdateRequestDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.AdminService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Admin",
        description = """
                ## Quản trị hệ thống
                
                Tất cả API Admin đều **yêu cầu JWT với ROLE_ADMIN**.
                Người dùng thông thường (ROLE_CUSTOMER) gọi vào đây sẽ nhận `403 Forbidden`.
                
                **Các chức năng:**
                - **Dashboard metrics**: Thống kê doanh thu, đơn hàng theo khoảng thời gian
                - **Quản lý tồn kho**: Nhập hàng vào kho (stock-in)
                - **Cập nhật trạng thái đơn**: Admin thay đổi trạng thái đơn hàng thủ công
                """
)
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(
            summary = "Lấy chỉ số dashboard",
            description = """
                    Trả về các chỉ số thống kê tổng quan cho khoảng thời gian chỉ định. **Yêu cầu JWT (ROLE_ADMIN).**
                    
                    **Ý nghĩa các chỉ số:**
                    - `totalRevenue`: Tổng doanh thu = Σ totalAmount của các đơn PAID trong khoảng
                    - `totalOrders`: Tổng số đơn hàng tạo (mọi trạng thái)
                    - `successfulOrders`: Đơn hàng DELIVERED
                    - `cancelledOrders`: Đơn hàng CANCELLED
                    - `totalProductsSold`: Tổng số lượng sản phẩm (items) đã bán qua đơn DELIVERED
                    
                    **Ví dụ URL:**
                    - Tháng 7/2026: `?startDate=2026-07-01&endDate=2026-07-31`
                    - Quý 3/2026: `?startDate=2026-07-01&endDate=2026-09-30`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chỉ số dashboard trong khoảng thời gian",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardMetricsResponseDto.class),
                            examples = @ExampleObject(name = "Tháng 7/2026", value = """
                                    {
                                      "totalRevenue": 125500000,
                                      "totalOrders": 842,
                                      "successfulOrders": 756,
                                      "cancelledOrders": 34,
                                      "totalProductsSold": 1523
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền Admin",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DashboardMetricsResponseDto> getDashboardMetrics(
            @Parameter(description = "Ngày bắt đầu thống kê (format: yyyy-MM-dd)", required = true, example = "2026-07-01")
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Ngày kết thúc thống kê (format: yyyy-MM-dd)", required = true, example = "2026-07-31")
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DashboardMetricsResponseDto response = adminService.getDashboardMetrics(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock/update")
    @Operation(
            summary = "Nhập hàng vào kho (Stock-In)"            
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nhập kho thành công",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Nhập kho thành công",
                                      "warehouseId": 1,
                                      "productVariantId": 101,
                                      "quantityAdded": 100,
                                      "newQuantity": 150
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền Admin",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho (WRH_001) hoặc biến thể sản phẩm (PRD_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateStock(@Valid @RequestBody StockUpdateRequestDto request) {
        adminService.updateWarehouseStock(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{orderId}/status")
    @Operation(
            summary = "Cập nhật trạng thái đơn hàng (Admin)",
            description = """
                    - `PENDING` → `APPROVED`: Xác nhận đơn hàng (sau khi thanh toán hoặc đơn COD)
                    - `APPROVED` → `SHIPPING`: Bắt đầu giao hàng
                    - `SHIPPING` → `DELIVERED`: Giao hàng thành công
                    - `PENDING/APPROVED` → `CANCELLED`: Hủy đơn hàng
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Chuyển đổi trạng thái không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền Admin",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng (ORD_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateOrderStatus(
            @Parameter(description = "ID đơn hàng cần cập nhật trạng thái", required = true, example = "1001")
            @PathVariable("orderId") Long orderId,
            @Parameter(description = "Trạng thái mới", required = true, example = "APPROVED",
                    schema = @Schema(allowableValues = {"PENDING", "APPROVED", "SHIPPING", "DELIVERED", "CANCELLED"}))
            @RequestParam("status") String status) {
        adminService.updateOrderStatus(orderId, status, "Cập nhật bởi Admin", "Admin");
        return ResponseEntity.ok().build();
    }
}
