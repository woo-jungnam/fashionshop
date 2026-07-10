package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Các chỉ số thống kê tổng quan trên dashboard quản trị")
public class DashboardMetricsResponseDto {

    @Schema(description = "Tổng doanh thu trong khoảng thời gian được chọn (đơn vị: VND). Chỉ tính các đơn hàng có paymentStatus = PAID.", example = "125500000", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalRevenue;

    @Schema(description = "Tổng số đơn hàng được tạo trong khoảng thời gian (bao gồm mọi trạng thái).", example = "842", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalOrders;

    @Schema(description = "Số đơn hàng thành công (trạng thái DELIVERED).", example = "756", accessMode = Schema.AccessMode.READ_ONLY)
    private Long successfulOrders;

    @Schema(description = "Số đơn hàng bị hủy (trạng thái CANCELLED).", example = "34", accessMode = Schema.AccessMode.READ_ONLY)
    private Long cancelledOrders;

    @Schema(description = "Tổng số lượng sản phẩm (items) đã bán trong khoảng thời gian — tính theo sum(quantity) của tất cả đơn DELIVERED.", example = "1523", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalProductsSold;
}
