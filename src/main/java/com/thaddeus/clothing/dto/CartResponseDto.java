package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin giỏ hàng của người dùng bao gồm danh sách sản phẩm đã chọn")
public class CartResponseDto {

    @Schema(description = "ID duy nhất của giỏ hàng.", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID người dùng sở hữu giỏ hàng.", example = "1")
    private Long userId;

    @Schema(description = "Danh sách các sản phẩm trong giỏ hàng. Mảng rỗng [] nếu giỏ trống.")
    private List<CartItemResponseDto> items;
}
