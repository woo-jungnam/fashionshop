package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.ProductRequestDto;
import com.thaddeus.clothing.dto.ProductResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(
        name = "Product",
        description = """
                ## Quản lý sản phẩm thời trang
                
                Toàn bộ CRUD cho sản phẩm:
                - **GET** (công khai — không cần token): Lấy danh sách, lấy theo ID
                - **POST/PUT/DELETE** (yêu cầu JWT): Tạo mới, cập nhật, xóa sản phẩm
                
                **Liên kết quan trọng:**
                - Biến thể (size/màu): `GET /api/v1/products/{id}/variants`
                - Hình ảnh sản phẩm: `GET /api/v1/products/{id}/images`
                - Bảng size: `GET /api/v1/products/{id}/size-guide`
                
                **Enum Status:**
                - `ACTIVE`: Đang bán, hiển thị website
                - `INACTIVE`: Ngừng bán, ẩn khỏi website
                - `DRAFT`: Nháp, chỉ Admin thấy
                """
)
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Tạo mới sản phẩm",
            description = """
                    Tạo mới một sản phẩm thời trang vào hệ thống. **Yêu cầu JWT.**
                    
                    **Lưu ý quan trọng:**
                    - `parentSku` nên là duy nhất — là SKU cha, các biến thể sẽ dùng SKU dạng `{parentSku}-{COLOR}-{SIZE}`
                    - `slug` dùng cho URL SEO: chỉ chữ thường, số, gạch ngang — không dấu tiếng Việt
                    - `categoryId` phải là ID danh mục tồn tại (lấy từ `GET /api/v1/categories`)
                    - `status` phải là một trong: `ACTIVE`, `INACTIVE`, `DRAFT`
                    
                    **Sau khi tạo sản phẩm**, tạo biến thể và upload ảnh qua các API riêng.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tạo sản phẩm thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Tạo Áo Polo thành công",
                                    value = """
                                            {
                                              "id": 42,
                                              "name": "Áo Polo Nam Cổ Bẻ Basic Premium",
                                              "parentSku": "POLO-BASIC-PREM-001",
                                              "slug": "ao-polo-nam-co-be-basic-premium",
                                              "shortDescription": "Cotton Pima 100%, co giãn 4 chiều",
                                              "description": "<p>Áo Polo nam cao cấp...</p>",
                                              "material": "100% Cotton Pima",
                                              "careInstructions": "Giặt máy ≤30°C",
                                              "categoryId": 1,
                                              "categoryName": "Áo Nam",
                                              "brandId": 1,
                                              "brandName": "Canifa",
                                              "status": "ACTIVE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ (thiếu field bắt buộc, status sai)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-07-10T12:00:00","status":400,"error":"Bad Request","errorCode":"VALIDATION_ERROR","message":"Tên sản phẩm không được trống","path":"/api/v1/products"}
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực — thiếu hoặc JWT không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "categoryId hoặc brandId không tồn tại",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto request) {
        ProductResponseDto response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin sản phẩm theo ID",
            description = """
                    Trả về thông tin chi tiết một sản phẩm theo ID. **API công khai — không cần JWT.**
                    
                    **Lưu ý:** API này chỉ trả về thông tin sản phẩm cha.
                    Để lấy biến thể, ảnh, bảng size — dùng các API:
                    - `GET /api/v1/products/{id}/variants`
                    - `GET /api/v1/products/{id}/images`
                    - `GET /api/v1/products/{id}/size-guide`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDto.class),
                            examples = @ExampleObject(name = "Sản phẩm Áo Polo", value = """
                                    {
                                      "id": 42,
                                      "name": "Áo Polo Nam Cổ Bẻ Basic Premium",
                                      "parentSku": "POLO-BASIC-PREM-001",
                                      "slug": "ao-polo-nam-co-be-basic-premium",
                                      "material": "100% Cotton Pima",
                                      "categoryId": 1,
                                      "categoryName": "Áo Nam",
                                      "status": "ACTIVE"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với ID đã cho",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp":"2026-07-10T12:00:00","status":404,"error":"Not Found","errorCode":"PRD_001","message":"Không tìm thấy sản phẩm","path":"/api/v1/products/9999"}
                                    """)))
    })
    public ResponseEntity<ProductResponseDto> getProductById(
            @Parameter(description = "ID sản phẩm cần tìm (số nguyên dương)", required = true, example = "42")
            @PathVariable("id") Long id) {
        ProductResponseDto response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách sản phẩm (có phân trang)",
            description = """
                    Trả về danh sách sản phẩm với phân trang và sắp xếp. **API công khai — không cần JWT.**
                    
                    **Cách dùng Pageable:**
                    - `?page=0&size=20` — Trang đầu, 20 sản phẩm (page bắt đầu từ 0, không phải 1)
                    - `?page=1&size=10&sort=name,asc` — Trang 2, 10 sản phẩm, sắp xếp tên A→Z
                    - `?page=0&size=50&sort=id,desc` — Trang đầu, 50 sản phẩm, mới nhất trước
                    
                    **Response trả về Page object** chứa: `content[]`, `totalElements`, `totalPages`, `pageNumber`...
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách sản phẩm (Page object)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Trang đầu 2 sản phẩm", value = """
                                    {
                                      "content": [
                                        {"id": 1, "name": "Áo Polo Nam Cổ Bẻ Basic", "parentSku": "POLO-BASIC-001", "categoryName": "Áo Nam", "status": "ACTIVE"},
                                        {"id": 2, "name": "Quần Jogger Nữ Lưng Thun", "parentSku": "JOGGER-NU-002", "categoryName": "Quần Nữ", "status": "ACTIVE"}
                                      ],
                                      "totalElements": 145,
                                      "totalPages": 8,
                                      "pageNumber": 0,
                                      "pageSize": 20,
                                      "first": true,
                                      "last": false,
                                      "empty": false
                                    }
                                    """)))
    })
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @Parameter(hidden = true) Pageable pageable) {
        Page<ProductResponseDto> response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cập nhật thông tin sản phẩm",
            description = """
                    Cập nhật toàn bộ thông tin sản phẩm theo ID (PUT = thay thế toàn bộ). **Yêu cầu JWT.**
                    
                    **Lưu ý:** Gửi đầy đủ tất cả field — field nào không gửi sẽ bị đặt null/default.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công, trả về thông tin sản phẩm mới",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponseDto> updateProduct(
            @Parameter(description = "ID sản phẩm cần cập nhật", required = true, example = "42")
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequestDto request) {
        ProductResponseDto response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Xóa sản phẩm",
            description = """
                    Xóa sản phẩm khỏi hệ thống theo ID. **Yêu cầu JWT.**
                    
                    ⚠️ **Cảnh báo Production:** Đây là xóa vĩnh viễn (hard delete).
                    Nếu sản phẩm có đơn hàng liên quan, thao tác này có thể gây lỗi database constraint.
                    **Khuyến nghị:** Thay vì xóa, hãy cập nhật `status = INACTIVE` để ẩn sản phẩm.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công — không có body response"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID sản phẩm cần xóa", required = true, example = "42")
            @PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
