package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin đặt hàng (checkout). Gọi API này sau khi người dùng đã có giỏ hàng và chọn địa chỉ giao hàng.")
public class OrderRequestDto {

    @NotBlank(message = "Địa chỉ giao hàng không được trống")
    @Schema(
            description = "Địa chỉ giao hàng đầy đủ — bao gồm số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố. " +
                    "Thường được lấy từ sổ địa chỉ của người dùng (GET /api/v1/users/addresses).",
            example = "Số 123 Đường Láng, Phường Láng Hạ, Quận Đống Đa, Hà Nội",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String shippingAddress;

    @Schema(
            description = "Mã giảm giá coupon (tuỳ chọn). Lấy danh sách coupon của người dùng từ GET /api/v1/user-coupons. " +
                    "Nếu coupon không hợp lệ hoặc hết hạn, hệ thống sẽ báo lỗi CPN_001. " +
                    "Nếu không dùng coupon, để null hoặc không truyền field này.",
            example = "SUMMER2026",
            nullable = true
    )
    private String couponCode;

    @NotNull(message = "Mã kho hàng không được trống")
    @Schema(
            description = "ID kho hàng xử lý đơn. Hệ thống sẽ trừ tồn kho tại kho này khi đặt hàng thành công. " +
                    "Thông thường Frontend chọn kho gần nhất với địa chỉ giao hàng.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long warehouseId;

    @NotNull(message = "Đơn vị vận chuyển không được trống")
    @Schema(
            description = "ID đơn vị vận chuyển (shipper/courier) để tính phí ship và điều phối giao hàng. " +
                    "Ví dụ: 1 = GHN, 2 = GHTK, 3 = ViettelPost.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long shipperId;

    @NotEmpty(message = "Đơn hàng phải chứa ít nhất một sản phẩm")
    @Valid
    @Schema(
            description = "Danh sách sản phẩm đặt hàng — phải có ít nhất 1 sản phẩm. " +
                    "Mỗi item chứa `productVariantId` và `quantity`. " +
                    "Hệ thống kiểm tra tồn kho cho từng sản phẩm — nếu không đủ hàng sẽ báo lỗi INV_001.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<OrderItemRequestDto> items;
}
