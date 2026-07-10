package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.ReviewRequestDto;
import com.thaddeus.clothing.dto.ReviewResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.ReviewService;
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
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(
        name = "Review",
        description = """
                ## Đánh giá sản phẩm
                
                Hệ thống đánh giá sau mua hàng với cơ chế **kiểm duyệt (PENDING → APPROVED/REJECTED)**.
                
                **Điều kiện để đánh giá:**
                - Người dùng phải có đơn hàng DELIVERED chứa sản phẩm đó
                - `orderItemId` là bắt buộc để xác minh giao dịch thực
                - Mỗi `orderItemId` chỉ được đánh giá **một lần**
                
                **Luồng đánh giá:**
                1. Người dùng vào trang "Lịch sử đơn hàng"
                2. Chọn đơn DELIVERED → nhấn "Đánh giá"
                3. Lấy `orderItemId` từ `OrderResponseDto.items[].id`
                4. Gọi `POST /api/v1/reviews` với `orderItemId` và `productId`
                5. Đánh giá có trạng thái `PENDING` — chờ Admin duyệt
                6. Admin duyệt: `POST /api/v1/reviews/{id}/approve` → chuyển sang `APPROVED`
                7. Đánh giá APPROVED mới hiển thị công khai trên trang sản phẩm
                """
)
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Gửi đánh giá sản phẩm",
            description = """
                    Gửi đánh giá (rating + comment) cho sản phẩm đã mua. **Yêu cầu JWT.**
                    
                    **Bắt buộc phải có `orderItemId`** — dùng để hệ thống xác nhận rằng
                    người dùng thực sự đã mua sản phẩm này (chống review giả).
                    
                    **Sau khi gửi:** Đánh giá có trạng thái `PENDING`.
                    Chỉ Admin mới thấy. Cần Admin duyệt (`POST /{id}/approve`) để hiển thị công khai.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gửi đánh giá thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewResponseDto.class),
                            examples = @ExampleObject(name = "Đánh giá 5 sao", value = """
                                    {
                                      "id": 301,
                                      "productId": 42,
                                      "productName": "Áo Polo Nam Cổ Bẻ Basic Premium",
                                      "userId": 1,
                                      "userFullName": "Nguyễn Văn A",
                                      "rating": 5,
                                      "comment": "Sản phẩm đẹp, chất vải tốt, đúng màu như ảnh. Giao hàng nhanh!",
                                      "mediaUrls": "https://cdn.thaddeus.vn/reviews/img1.jpg",
                                      "status": "PENDING"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc đã đánh giá orderItem này rồi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm hoặc orderItem",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReviewResponseDto> submitReview(
            @Parameter(description = "ID người dùng gửi đánh giá", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Valid @RequestBody ReviewRequestDto request
    ) {
        ReviewResponseDto response = reviewService.submitReview(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    @Operation(
            summary = "Lấy danh sách đánh giá của sản phẩm",
            description = """
                    Trả về danh sách đánh giá đã được duyệt (APPROVED) của một sản phẩm. **API công khai.**
                    
                    Hỗ trợ phân trang — sử dụng `?page=0&size=10&sort=id,desc` để lấy đánh giá mới nhất trước.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách đánh giá (Page object)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Ví dụ đánh giá sản phẩm 42", value = """
                                    {
                                      "content": [
                                        {
                                          "id": 301,
                                          "productId": 42,
                                          "userId": 1,
                                          "userFullName": "Nguyễn Văn A",
                                          "rating": 5,
                                          "comment": "Sản phẩm đẹp, chất vải tốt!",
                                          "status": "APPROVED"
                                        },
                                        {
                                          "id": 298,
                                          "productId": 42,
                                          "userId": 5,
                                          "userFullName": "Trần Thị B",
                                          "rating": 4,
                                          "comment": "Màu đẹp, size đúng, sẽ mua lại.",
                                          "status": "APPROVED"
                                        }
                                      ],
                                      "totalElements": 28,
                                      "totalPages": 3
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<ReviewResponseDto>> getProductReviews(
            @Parameter(description = "ID sản phẩm cần lấy đánh giá", required = true, example = "42")
            @PathVariable("productId") Long productId,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<ReviewResponseDto> response = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Duyệt đánh giá (Admin)",
            description = """
                    Admin duyệt một đánh giá — chuyển trạng thái từ `PENDING` sang `APPROVED`. **Yêu cầu JWT (Admin).**
                    
                    Chỉ đánh giá có trạng thái `APPROVED` mới hiển thị công khai trên trang sản phẩm.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duyệt thành công, trả về đánh giá đã cập nhật",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 301,
                                      "productId": 42,
                                      "userId": 1,
                                      "userFullName": "Nguyễn Văn A",
                                      "rating": 5,
                                      "comment": "Sản phẩm đẹp, chất vải tốt!",
                                      "status": "APPROVED"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đánh giá",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReviewResponseDto> approveReview(
            @Parameter(description = "ID đánh giá cần duyệt", required = true, example = "301")
            @PathVariable("id") Long id) {
        ReviewResponseDto response = reviewService.approveReview(id);
        return ResponseEntity.ok(response);
    }
}
