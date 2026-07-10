package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin một sản phẩm trong yêu cầu đổi trả")
public class ReturnItemResponseDto {

    @Schema(description = "ID duy nhất của dòng đổi trả.", example = "77", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID dòng hàng trong đơn hàng gốc.", example = "501")
    private Long orderItemId;

    @Schema(description = "Tên sản phẩm.", example = "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M")
    private String productName;

    @Schema(description = "Mã SKU biến thể sản phẩm.", example = "POLO-BASIC-PREM-001-RED-M")
    private String sku;

    @Schema(description = "Số lượng đổi trả.", example = "1")
    private Integer quantity;

    @Schema(description = "Tình trạng hàng khi trả lại: NGUYEN_TAG = còn nguyên tag, DA_GIAT = đã qua giặt, HONG = bị hỏng.", example = "NGUYEN_TAG", allowableValues = {"NGUYEN_TAG", "DA_GIAT", "HONG"})
    private String conditionState;
}
