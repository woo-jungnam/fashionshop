package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.CategoryRequestDto;
import com.thaddeus.clothing.dto.CategoryResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.CategoryService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(
        name = "Category",
        description = """
                ## Quản lý danh mục sản phẩm
                
                Danh mục hỗ trợ cấu trúc **cây phân cấp** (cha → con):
                - Ví dụ: `Quần Áo Nam` (cấp 1) → `Áo Polo` (cấp 2) → `Polo tay ngắn` (cấp 3)
                - `parentId = null`: Danh mục gốc cấp 1
                - `parentId = {id}`: Danh mục con của danh mục có id đó
                
                **Quyền truy cập:**
                - GET: Công khai, không cần JWT
                - POST/PUT/DELETE: Yêu cầu JWT
                
                **Dữ liệu danh mục mẫu:**
                - ID 1: Áo Nam | ID 2: Quần Nữ | ID 3: Phụ kiện
                """
)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Tạo mới danh mục",
            description = """
                    Tạo mới danh mục sản phẩm. **Yêu cầu JWT.**
                    
                    - `parentId = null` → Tạo danh mục gốc cấp 1
                    - `parentId = {id}` → Tạo danh mục con (cấp 2, 3...)
                    - `slug` phải là duy nhất trong hệ thống
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo danh mục thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class),
                            examples = {
                                    @ExampleObject(name = "Danh mục gốc", summary = "Tạo danh mục cấp 1", value = """
                                            {"id": 1, "name": "Áo Nam", "slug": "ao-nam", "parentId": null}
                                            """),
                                    @ExampleObject(name = "Danh mục con", summary = "Tạo danh mục cấp 2", value = """
                                            {"id": 5, "name": "Áo Polo Nam", "slug": "ao-polo-nam", "parentId": 1}
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy danh mục theo ID",
            description = "Trả về thông tin danh mục theo ID. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy danh mục",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {"id": 1, "name": "Áo Nam", "slug": "ao-nam", "parentId": null}
                                    """))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @Parameter(description = "ID danh mục cần tìm", required = true, example = "1")
            @PathVariable("id") Long id) {
        CategoryResponseDto response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Lấy toàn bộ danh mục",
            description = """
                    Trả về danh sách tất cả danh mục trong hệ thống. **API công khai — không phân trang.**
                    
                    **Cách xây dựng cây danh mục phía Frontend:**
                    1. Lọc các item có `parentId = null` → danh mục cấp 1
                    2. Với mỗi danh mục cấp 1, tìm các item có `parentId = id` → danh mục cấp 2
                    3. Lặp đệ quy để xây dựng cây hoàn chỉnh
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách toàn bộ danh mục",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDto.class)),
                            examples = @ExampleObject(name = "Ví dụ danh sách", value = """
                                    [
                                      {"id": 1, "name": "Áo Nam", "slug": "ao-nam", "parentId": null},
                                      {"id": 2, "name": "Quần Nữ", "slug": "quan-nu", "parentId": null},
                                      {"id": 3, "name": "Phụ kiện", "slug": "phu-kien", "parentId": null},
                                      {"id": 5, "name": "Áo Polo Nam", "slug": "ao-polo-nam", "parentId": 1},
                                      {"id": 6, "name": "Áo Sơ Mi Nam", "slug": "ao-so-mi-nam", "parentId": 1}
                                    ]
                                    """)))
    })
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cập nhật danh mục", description = "Cập nhật thông tin danh mục theo ID. **Yêu cầu JWT.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Parameter(description = "ID danh mục cần cập nhật", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Xóa danh mục", description = "Xóa danh mục theo ID. **Yêu cầu JWT.** ⚠️ Cẩn thận: không xóa danh mục đang có sản phẩm.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID danh mục cần xóa", required = true, example = "1")
            @PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
