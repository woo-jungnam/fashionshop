package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.ReturnRequestDto;
import com.thaddeus.clothing.dto.ReturnResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.ReturnService;
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
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Return & Refund",
        description = """
                ## Yêu cầu đổi trả hàng
                
                Tất cả API đổi trả đều **yêu cầu JWT**.
                
                **Điều kiện đổi trả:**
                - Đơn hàng phải ở trạng thái `DELIVERED`
                - Trong vòng 7 ngày kể từ ngày giao hàng
                - Sản phẩm phải còn nguyên vẹn (khuyến nghị còn nguyên tag)
                
                **Luồng đổi trả:**
                1. Người dùng gửi yêu cầu → `POST /api/v1/returns?userId=1` → `status: PENDING`
                2. Admin xem và xử lý: `PUT /api/v1/returns/{id}/status`
                   - Duyệt: `APPROVED` → bắt đầu quy trình hoàn hàng
                   - Từ chối: `REJECTED`
                3. Sau khi nhận hàng hoàn, Admin hoàn tiền: `COMPLETED`
                
                **Trạng thái (ReturnStatus):** `PENDING` → `APPROVED` → `COMPLETED` hoặc `REJECTED`
                """
)
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    @Operation(
            summary = "Tạo yêu cầu đổi trả",
            description = """
                    Người dùng gửi yêu cầu đổi trả sản phẩm đã mua. **Yêu cầu JWT.**
                    
                    **Bắt buộc:** `orderId` phải là đơn hàng DELIVERED của chính người dùng này.
                    
                    **Có thể đổi trả một phần:** Chỉ cần chọn các `orderItemId` muốn đổi trả,
                    không cần phải đổi trả toàn bộ đơn hàng.
                    
                    **Khuyến nghị:** Đính kèm ảnh/video bằng chứng (`evidenceUrls`) để được xét duyệt nhanh hơn.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo yêu cầu đổi trả thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnResponseDto.class),
                            examples = @ExampleObject(name = "Yêu cầu đổi trả mới", value = """
                                    {
                                      "id": 88,
                                      "orderId": 1001,
                                      "reason": "Sản phẩm bị lỗi đường may ở tay áo trái",
                                      "evidenceUrls": "https://cdn.thaddeus.vn/returns/evidence1.jpg",
                                      "status": "PENDING",
                                      "items": [
                                        {
                                          "id": 77,
                                          "orderItemId": 501,
                                          "productName": "Áo Polo Nam Cổ Bẻ Basic Premium — Màu Đỏ Size M",
                                          "sku": "POLO-BASIC-PREM-001-RED-M",
                                          "quantity": 1,
                                          "conditionState": "NGUYEN_TAG"
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Đơn hàng chưa DELIVERED, đã quá 7 ngày, hoặc dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng hoặc người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReturnResponseDto> createReturnRequest(
            @Parameter(description = "ID người dùng gửi yêu cầu đổi trả", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Valid @RequestBody ReturnRequestDto request) {
        ReturnResponseDto response = returnService.createReturnRequest(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin yêu cầu đổi trả theo ID",
            description = "Trả về chi tiết một yêu cầu đổi trả theo ID. **Yêu cầu JWT.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy yêu cầu đổi trả",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy yêu cầu đổi trả",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReturnResponseDto> getReturnById(
            @Parameter(description = "ID yêu cầu đổi trả", required = true, example = "88")
            @PathVariable("id") Long id) {
        ReturnResponseDto response = returnService.getReturnById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Lấy danh sách đổi trả của người dùng",
            description = "Lấy toàn bộ lịch sử đổi trả hàng của người dùng (phân trang). **Yêu cầu JWT.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách yêu cầu đổi trả",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<ReturnResponseDto>> getReturnsByUser(
            @Parameter(description = "ID người dùng cần lấy lịch sử đổi trả", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<ReturnResponseDto> response = returnService.getReturnsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái yêu cầu đổi trả (Admin)",
            description = """
                    Admin xử lý yêu cầu đổi trả — thay đổi trạng thái. **Yêu cầu JWT (Admin).**
                    
                    **Các chuyển đổi trạng thái hợp lệ:**
                    - `PENDING` → `APPROVED`: Chấp nhận yêu cầu, bắt đầu xử lý hoàn hàng
                    - `PENDING` → `REJECTED`: Từ chối yêu cầu (không đủ điều kiện, ảnh không rõ...)
                    - `APPROVED` → `COMPLETED`: Đã nhận hàng hoàn, hoàn tất quy trình
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Trạng thái chuyển đổi không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy yêu cầu đổi trả",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReturnResponseDto> updateReturnStatus(
            @Parameter(description = "ID yêu cầu đổi trả cần cập nhật", required = true, example = "88")
            @PathVariable("id") Long id,
            @Parameter(description = "Trạng thái mới (APPROVED, REJECTED, COMPLETED)", required = true, example = "APPROVED",
                    schema = @Schema(allowableValues = {"APPROVED", "REJECTED", "COMPLETED"}))
            @RequestParam("status") String status) {
        ReturnResponseDto response = returnService.updateReturnStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
