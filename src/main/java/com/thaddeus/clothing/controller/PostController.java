package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.PostResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(
        name = "Blog Post",
        description = """
                ## Blog & Bài viết thời trang
                
                Các bài viết blog, hướng dẫn phong cách, xu hướng thời trang. **API công khai — không cần JWT.**
                
                **Dùng cho:**
                - Trang blog của website thương mại điện tử
                - SEO: Mỗi bài có `slug` làm URL thân thiện
                - Nội dung marketing: Xu hướng thời trang, cách phối đồ...
                """
)
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(
            summary = "Lấy danh sách bài viết (có phân trang)",
            description = """
                    Trả về danh sách bài viết với phân trang và sắp xếp. **API công khai.**
                    
                    **Ví dụ URL:**
                    - `?page=0&size=6&sort=createdAt,desc` — 6 bài viết mới nhất (trang blog)
                    - `?page=0&size=3` — 3 bài nổi bật cho trang chủ
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách bài viết (Page object)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Danh sách blog", value = """
                                    {
                                      "content": [
                                        {
                                          "id": 10,
                                          "title": "Xu hướng thời trang Hè 2026: Màu Pastel lên ngôi",
                                          "slug": "xu-huong-thoi-trang-he-2026-mau-pastel-len-ngoi",
                                          "thumbnailUrl": "https://cdn.thaddeus.vn/blog/pastel-2026.jpg",
                                          "shortDescription": "Khám phá những gam màu pastel dịu dàng đang làm mưa làm gió...",
                                          "author": "Nguyễn Hà Linh",
                                          "categoryName": "Xu hướng thời trang",
                                          "createdAt": "2026-06-15T09:30:00"
                                        }
                                      ],
                                      "totalElements": 24,
                                      "totalPages": 4
                                    }
                                    """)))
    })
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @Parameter(hidden = true) Pageable pageable) {
        Page<PostResponseDto> response = postService.getAllPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy bài viết theo ID",
            description = "Trả về nội dung đầy đủ của một bài viết theo ID. **API công khai.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy bài viết",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponseDto.class),
                            examples = @ExampleObject(name = "Bài viết đầy đủ", value = """
                                    {
                                      "id": 10,
                                      "title": "Xu hướng thời trang Hè 2026: Màu Pastel lên ngôi",
                                      "slug": "xu-huong-thoi-trang-he-2026-mau-pastel-len-ngoi",
                                      "thumbnailUrl": "https://cdn.thaddeus.vn/blog/pastel-2026.jpg",
                                      "shortDescription": "Khám phá những gam màu pastel dịu dàng...",
                                      "content": "<h2>Tại sao Pastel lên ngôi?</h2><p>...</p>",
                                      "author": "Nguyễn Hà Linh",
                                      "categoryName": "Xu hướng thời trang",
                                      "createdAt": "2026-06-15T09:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostResponseDto> getPostById(
            @Parameter(description = "ID bài viết cần xem", required = true, example = "10")
            @PathVariable("id") Long id) {
        PostResponseDto response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }
}
