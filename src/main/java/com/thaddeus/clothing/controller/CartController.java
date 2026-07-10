package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.CartItemRequestDto;
import com.thaddeus.clothing.dto.CartResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.CartService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Cart",
        description = """
                ## Quản lý giỏ hàng
                
                Tất cả API giỏ hàng đều **yêu cầu JWT**. Mỗi người dùng có một giỏ hàng duy nhất.
                
                **Luồng mua hàng:**
                1. Lấy giỏ hàng: `GET /api/v1/carts?userId=1`
                2. Thêm sản phẩm: `POST /api/v1/carts/items?userId=1`
                3. Cập nhật số lượng: `PUT /api/v1/carts/items/{itemId}?userId=1&quantity=3`
                4. Xóa sản phẩm: `DELETE /api/v1/carts/items/{itemId}?userId=1`
                5. Thanh toán: `POST /api/v1/orders?userId=1`
                6. Sau thanh toán, xóa giỏ: `DELETE /api/v1/carts?userId=1`
                
                **Lưu ý quan trọng:** `productVariantId` là ID biến thể (size/màu), không phải ID sản phẩm cha.
                Lấy `variantId` từ `GET /api/v1/products/{id}/variants`.
                """
)
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(
            summary = "Lấy giỏ hàng của người dùng",
            description = """
                    Trả về toàn bộ giỏ hàng hiện tại của người dùng. **Yêu cầu JWT.**
                    
                    Nếu người dùng chưa có giỏ hàng, hệ thống tự tạo và trả về giỏ rỗng (`items: []`).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giỏ hàng của người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDto.class),
                            examples = {
                                    @ExampleObject(name = "Giỏ hàng có sản phẩm", value = """
                                            {
                                              "id": 10,
                                              "userId": 1,
                                              "items": [
                                                {
                                                  "id": 55,
                                                  "productVariantId": 101,
                                                  "sku": "POLO-BASIC-PREM-001-RED-M",
                                                  "productName": "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M",
                                                  "price": 299000,
                                                  "salePrice": 249000,
                                                  "quantity": 2
                                                }
                                              ]
                                            }
                                            """),
                                    @ExampleObject(name = "Giỏ hàng rỗng", value = """
                                            {"id": 10, "userId": 1, "items": []}
                                            """)
                            })),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CartResponseDto> getCart(
            @Parameter(description = "ID người dùng cần lấy giỏ hàng", required = true, example = "1")
            @RequestParam("userId") Long userId) {
        CartResponseDto response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @Operation(
            summary = "Thêm sản phẩm vào giỏ hàng",
            description = """
                    Thêm một biến thể sản phẩm vào giỏ hàng với số lượng chỉ định. **Yêu cầu JWT.**
                    
                    **Quan trọng:** `productVariantId` là ID biến thể (size/màu cụ thể), không phải ID sản phẩm.
                    
                    **Nếu sản phẩm đã có trong giỏ:** Số lượng được **cộng thêm** (không thay thế).
                    Ví dụ: giỏ có 2 → thêm 3 → giỏ sẽ có 5.
                    
                    **Kiểm tra tồn kho:** Nếu số lượng trong giỏ vượt tồn kho thực tế, trả về lỗi `INV_001`.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thêm sản phẩm thành công, trả về giỏ hàng cập nhật",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 10,
                                      "userId": 1,
                                      "items": [
                                        {
                                          "id": 55,
                                          "productVariantId": 101,
                                          "sku": "POLO-BASIC-PREM-001-RED-M",
                                          "productName": "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M",
                                          "price": 299000,
                                          "salePrice": 249000,
                                          "quantity": 2
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Sản phẩm hết hàng (INV_001) hoặc dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-07-10T12:00:00","status":400,"error":"Bad Request","errorCode":"INV_001","message":"Sản phẩm đã hết hàng hoặc số lượng tồn kho khả dụng không đủ","path":"/api/v1/carts/items"}
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy biến thể sản phẩm (PRD_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CartResponseDto> addItemToCart(
            @Parameter(description = "ID người dùng muốn thêm sản phẩm vào giỏ", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Valid @RequestBody CartItemRequestDto request) {
        CartResponseDto response = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{itemId}")
    @Operation(
            summary = "Cập nhật số lượng sản phẩm trong giỏ",
            description = """
                    Thay đổi số lượng của một sản phẩm đã có trong giỏ hàng. **Yêu cầu JWT.**
                    
                    `itemId` là ID của dòng hàng trong giỏ — lấy từ `CartResponseDto.items[].id`.
                    
                    **Giới hạn:** `quantity` phải >= 1. Để xóa sản phẩm, dùng `DELETE /carts/items/{itemId}`.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật số lượng thành công, trả về giỏ hàng cập nhật",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Số lượng không hợp lệ hoặc vượt quá tồn kho",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy dòng hàng trong giỏ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CartResponseDto> updateCartItem(
            @Parameter(description = "ID người dùng", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Parameter(description = "ID dòng hàng trong giỏ (CartItem.id) — lấy từ items[].id", required = true, example = "55")
            @PathVariable("itemId") Long itemId,
            @Parameter(description = "Số lượng mới (tối thiểu 1)", required = true, example = "3")
            @RequestParam("quantity") Integer quantity) {
        CartResponseDto response = cartService.updateCartItem(userId, itemId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(
            summary = "Xóa một sản phẩm khỏi giỏ hàng",
            description = "Xóa một dòng hàng cụ thể khỏi giỏ theo `itemId`. **Yêu cầu JWT.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thành công, trả về giỏ hàng sau khi xóa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy dòng hàng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CartResponseDto> removeCartItem(
            @Parameter(description = "ID người dùng", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Parameter(description = "ID dòng hàng cần xóa", required = true, example = "55")
            @PathVariable("itemId") Long itemId) {
        CartResponseDto response = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(
            summary = "Xóa toàn bộ giỏ hàng",
            description = """
                    Xóa tất cả sản phẩm trong giỏ hàng của người dùng. **Yêu cầu JWT.**
                    
                    Thường được gọi **sau khi thanh toán thành công** để làm trống giỏ hàng.
                    Trả về `204 No Content` — không có body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa toàn bộ giỏ hàng thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "ID người dùng cần xóa giỏ hàng", required = true, example = "1")
            @RequestParam("userId") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
