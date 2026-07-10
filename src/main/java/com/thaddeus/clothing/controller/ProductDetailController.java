package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.ProductImageResponseDto;
import com.thaddeus.clothing.dto.ProductVariantResponseDto;
import com.thaddeus.clothing.dto.SizeGuideResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.ProductDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}")
@RequiredArgsConstructor
@Tag(
        name = "Product Detail",
        description = """
                ## Chi tiết sản phẩm: Biến thể, Hình ảnh, Bảng size
                
                Các API công khai (không cần JWT) để lấy thông tin chi tiết sản phẩm:
                - **Biến thể** (`/variants`): Danh sách size/màu, giá, tồn kho
                - **Hình ảnh** (`/images`): Gallery ảnh sản phẩm
                - **Bảng size** (`/size-guide`): Hướng dẫn chọn size
                
                **Flow trang chi tiết sản phẩm:**
                1. Lấy info sản phẩm: `GET /api/v1/products/{id}`
                2. Lấy biến thể: `GET /api/v1/products/{id}/variants`
                3. Lấy ảnh: `GET /api/v1/products/{id}/images`
                4. Người dùng chọn size/màu → lấy `variantId` → thêm vào giỏ
                """
)
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    @GetMapping("/variants")
    @Operation(
            summary = "Lấy danh sách biến thể sản phẩm (size/màu)",
            description = """
                    Trả về tất cả biến thể (kết hợp size × màu) của sản phẩm theo `productId`. **API công khai.**
                    
                    **Khi nào dùng:**
                    - Trang chi tiết sản phẩm — hiển thị bảng chọn size và màu sắc
                    - Trước khi thêm vào giỏ hàng (cần `id` của biến thể, không phải `productId`)
                    
                    **Quy trình thêm vào giỏ:**
                    1. Gọi API này → lấy danh sách biến thể
                    2. Người dùng chọn Size M, Color Đỏ → `id = 101`
                    3. Gọi `POST /api/v1/carts/items` với `productVariantId: 101`
                    
                    **`salePrice` vs `price`:** Nếu `salePrice` không null → hiển thị giá sale, gạch giá gốc
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách biến thể sản phẩm",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductVariantResponseDto.class)),
                            examples = @ExampleObject(
                                    name = "Biến thể Áo Polo #42",
                                    value = """
                                            [
                                              {
                                                "id": 101,
                                                "sku": "POLO-BASIC-PREM-001-RED-S",
                                                "barcode": "8938505970001",
                                                "price": 299000,
                                                "salePrice": 249000,
                                                "status": "ACTIVE",
                                                "attributes": ["Color: Đỏ", "Size: S"]
                                              },
                                              {
                                                "id": 102,
                                                "sku": "POLO-BASIC-PREM-001-RED-M",
                                                "barcode": "8938505970002",
                                                "price": 299000,
                                                "salePrice": 249000,
                                                "status": "ACTIVE",
                                                "attributes": ["Color: Đỏ", "Size: M"]
                                              },
                                              {
                                                "id": 103,
                                                "sku": "POLO-BASIC-PREM-001-NAVY-L",
                                                "barcode": "8938505970003",
                                                "price": 299000,
                                                "salePrice": null,
                                                "status": "OUT_OF_STOCK",
                                                "attributes": ["Color: Navy", "Size: L"]
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với productId đã cho",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductVariantResponseDto>> getProductVariants(
            @Parameter(description = "ID sản phẩm cha cần lấy biến thể", required = true, example = "42")
            @PathVariable("productId") Long productId) {
        List<ProductVariantResponseDto> response = productDetailService.getProductVariants(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/images")
    @Operation(
            summary = "Lấy danh sách hình ảnh sản phẩm",
            description = """
                    Trả về tất cả hình ảnh trong gallery của sản phẩm. **API công khai.**
                    
                    **Sắp xếp:** Ảnh được trả về theo `displayOrder` tăng dần.
                    Ảnh có `isMain = true` là ảnh đại diện chính (thumbnail) — hiển thị ở danh sách sản phẩm.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách hình ảnh sản phẩm",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductImageResponseDto.class)),
                            examples = @ExampleObject(
                                    name = "Ảnh Áo Polo #42",
                                    value = """
                                            [
                                              {
                                                "id": 201,
                                                "imageUrl": "https://cdn.thaddeus.vn/products/polo-basic-001-main.jpg",
                                                "displayOrder": 1,
                                                "isMain": true
                                              },
                                              {
                                                "id": 202,
                                                "imageUrl": "https://cdn.thaddeus.vn/products/polo-basic-001-back.jpg",
                                                "displayOrder": 2,
                                                "isMain": false
                                              },
                                              {
                                                "id": 203,
                                                "imageUrl": "https://cdn.thaddeus.vn/products/polo-basic-001-detail.jpg",
                                                "displayOrder": 3,
                                                "isMain": false
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductImageResponseDto>> getProductImages(
            @Parameter(description = "ID sản phẩm cần lấy ảnh", required = true, example = "42")
            @PathVariable("productId") Long productId) {
        List<ProductImageResponseDto> response = productDetailService.getProductImages(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/size-guide")
    @Operation(
            summary = "Lấy bảng hướng dẫn size sản phẩm",
            description = """
                    Trả về bảng hướng dẫn chọn size cho sản phẩm. **API công khai.**
                    
                    `specifications` chứa thông số số đo chi tiết theo từng size (JSON string hoặc văn bản).
                    Frontend parse và hiển thị thành bảng kích thước.
                    
                    **Trả về null nếu sản phẩm không có bảng size** — Frontend cần xử lý trường hợp này.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bảng hướng dẫn size",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SizeGuideResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Bảng size Áo Nam",
                                    value = """
                                            {
                                              "id": 5,
                                              "name": "Bảng Size Áo Nam",
                                              "imageUrl": "https://cdn.thaddeus.vn/size-guide/ao-nam-2026.jpg",
                                              "specifications": "{\\"S\\": {\\"ngực\\": \\"88cm\\", \\"eo\\": \\"76cm\\", \\"hông\\": \\"90cm\\"}, \\"M\\": {\\"ngực\\": \\"92cm\\", \\"eo\\": \\"80cm\\", \\"hông\\": \\"94cm\\"}, \\"L\\": {\\"ngực\\": \\"96cm\\", \\"eo\\": \\"84cm\\", \\"hông\\": \\"98cm\\"}}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm hoặc sản phẩm chưa có bảng size",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SizeGuideResponseDto> getSizeGuide(
            @Parameter(description = "ID sản phẩm cần lấy bảng size", required = true, example = "42")
            @PathVariable("productId") Long productId) {
        SizeGuideResponseDto response = productDetailService.getSizeGuide(productId);
        return ResponseEntity.ok(response);
    }
}
