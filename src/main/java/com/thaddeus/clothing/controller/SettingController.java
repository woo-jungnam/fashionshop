package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.SettingRequestDto;
import com.thaddeus.clothing.dto.SettingResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.SettingService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(
        name = "System Setting",
        description = """
                ## Cấu hình hệ thống (Settings)
                
                Quản lý các cấu hình dạng Key-Value trong hệ thống (ví dụ: tên website, ngưỡng free ship, bật/tắt cổng thanh toán).
                
                **Quyền truy cập:**
                - GET: Công khai (không yêu cầu xác thực)
                - POST: Yêu cầu JWT (Admin)
                """
)
public class SettingController {

    private final SettingService settingService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Lưu hoặc cập nhật cấu hình",
            description = """
                    Admin lưu hoặc cập nhật cấu hình hệ thống. **Yêu cầu JWT (Admin).**
                    
                    Nếu `key` đã tồn tại, hệ thống thực hiện cập nhật `value` và `description` (Upsert).
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lưu cấu hình thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "key": "shipping.free_threshold",
                                      "value": "500000",
                                      "description": "Ngưỡng miễn phí vận chuyển (VND)"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền Admin",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SettingResponseDto> saveSetting(@Valid @RequestBody SettingRequestDto request) {
        SettingResponseDto response = settingService.saveSetting(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{key}")
    @Operation(
            summary = "Lấy cấu hình theo Key",
            description = "Lấy thông tin một cấu hình theo khóa key. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy cấu hình",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "key": "shipping.free_threshold",
                                      "value": "500000",
                                      "description": "Ngưỡng miễn phí vận chuyển (VND)"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cấu hình với key đã cho",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SettingResponseDto> getSettingByKey(
            @Parameter(description = "Khóa key của cấu hình cần lấy", required = true, example = "shipping.free_threshold")
            @PathVariable("key") String key) {
        SettingResponseDto response = settingService.getSettingByKey(key);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả cấu hình",
            description = "Trả về danh sách tất cả các thiết lập/cấu hình đang có trong hệ thống. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách cấu hình",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SettingResponseDto.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {"id": 1, "key": "shipping.free_threshold", "value": "500000", "description": "Ngưỡng miễn phí vận chuyển (VND)"},
                                      {"id": 2, "key": "site.name", "value": "Thaddeus Clothing", "description": "Tên website hiển thị"}
                                    ]
                                    """)))
    })
    public ResponseEntity<List<SettingResponseDto>> getAllSettings() {
        List<SettingResponseDto> response = settingService.getAllSettings();
        return ResponseEntity.ok(response);
    }
}
