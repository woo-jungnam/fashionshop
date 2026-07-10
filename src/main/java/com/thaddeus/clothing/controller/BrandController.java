package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.BrandRequestDto;
import com.thaddeus.clothing.dto.BrandResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.BrandService;
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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(
        name = "Brand",
        description = """
                ## Quản lý thương hiệu sản phẩm
                
                Quản lý các hãng sản xuất, thương hiệu thời trang được phân phối trên hệ thống (ví dụ: Nike, Adidas, Zara...).
                
                **Quyền truy cập:**
                - GET: Công khai, không cần JWT
                - POST/PUT/DELETE: Yêu cầu JWT (Vai trò quản trị viên)
                """
)
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Tạo mới thương hiệu"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thương hiệu thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandResponseDto.class),
                            examples = @ExampleObject(name = "Thương hiệu mới", value = """
                                    {"id": 1, "name": "Nike", "logoUrl": "https://example.com/logos/nike.png", "origin": "USA"}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Tên thương hiệu đã tồn tại hoặc dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc không đủ quyền hạn",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BrandResponseDto> createBrand(@Valid @RequestBody BrandRequestDto request) {
        BrandResponseDto response = brandService.createBrand(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thương hiệu theo ID",
            description = "Lấy thông tin chi tiết của thương hiệu qua ID. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thương hiệu",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {"id": 1, "name": "Nike", "logoUrl": "https://example.com/logos/nike.png", "origin": "USA"}
                                    """))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BrandResponseDto> getBrandById(
            @Parameter(description = "ID thương hiệu cần lấy thông tin", required = true, example = "1")
            @PathVariable("id") Long id) {
        BrandResponseDto response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả thương hiệu",
            description = "Trả về danh sách tất cả thương hiệu hiện có. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách thương hiệu",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BrandResponseDto.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {"id": 1, "name": "Nike", "logoUrl": "https://example.com/logos/nike.png", "origin": "USA"},
                                      {"id": 2, "name": "Adidas", "logoUrl": "https://example.com/logos/adidas.png", "origin": "Germany"}
                                    ]
                                    """)))
    })
    public ResponseEntity<List<BrandResponseDto>> getAllBrands() {
        List<BrandResponseDto> response = brandService.getAllBrands();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cập nhật thương hiệu",
            description = "Cập nhật thông tin thương hiệu theo ID. **Yêu cầu JWT.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tên thương hiệu trùng lặp",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc không đủ quyền hạn",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BrandResponseDto> updateBrand(
            @Parameter(description = "ID thương hiệu cần cập nhật", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody BrandRequestDto request) {
        BrandResponseDto response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Xóa thương hiệu",
            description = "Xóa thương hiệu theo ID. **Yêu cầu JWT.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thương hiệu thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc không đủ quyền hạn",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteBrand(
            @Parameter(description = "ID thương hiệu cần xóa", required = true, example = "1")
            @PathVariable("id") Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
