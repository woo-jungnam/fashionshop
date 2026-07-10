package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.AuthRequestDto;
import com.thaddeus.clothing.dto.AuthResponseDto;
import com.thaddeus.clothing.dto.RegisterRequestDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập hệ thống",
            description = """
                    Xác thực email + mật khẩu, trả về **JWT Access Token** dùng cho toàn bộ API yêu cầu xác thực.
                    
                    **Sau khi nhận được token:**
                    - Lưu token vào `localStorage` hoặc `Cookie` phía Frontend
                    - Gắn vào header mỗi request: `Authorization: Bearer <accessToken>`
                    - Token hết hạn sau 24 giờ — cần đăng nhập lại để lấy token mới
                    
                    **Các lỗi thường gặp:**
                    - `400`: Email hoặc mật khẩu để trống / sai format email
                    - `401`: Sai mật khẩu hoặc email không tồn tại trong hệ thống
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công — trả về JWT Access Token và thông tin tài khoản",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Admin đăng nhập thành công",
                                            summary = "Response cho tài khoản Admin",
                                            value = """
                                                    {
                                                      "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0aGFkZGV1cy52biIsImlhdCI6MTc1MjE0OTAwMCwiZXhwIjoxNzUyMjM1NDAwfQ.signature",
                                                      "tokenType": "Bearer",
                                                      "email": "admin@thaddeus.vn",
                                                      "roles": ["ROLE_ADMIN"]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Customer đăng nhập thành công",
                                            summary = "Response cho tài khoản Customer",
                                            value = """
                                                    {
                                                      "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lckB0aGFkZGV1cy52biIsImlhdCI6MTc1MjE0OTAwMH0.signature",
                                                      "tokenType": "Bearer",
                                                      "email": "customer@thaddeus.vn",
                                                      "roles": ["ROLE_CUSTOMER"]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ (email trống, email sai format, mật khẩu trống)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Email trống",
                                    value = """
                                            {
                                              "timestamp": "2026-07-10T12:00:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "errorCode": "VALIDATION_ERROR",
                                              "message": "Email đăng nhập không được trống",
                                              "path": "/api/v1/auth/login"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Sai mật khẩu hoặc email không tồn tại trong hệ thống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Sai mật khẩu",
                                    value = """
                                            {
                                              "timestamp": "2026-07-10T12:00:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "errorCode": "UNAUTHORIZED",
                                              "message": "Bad credentials",
                                              "path": "/api/v1/auth/login"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống nội bộ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Đăng ký tài khoản mới",
            description = """
                    Tạo mới tài khoản khách hàng (`ROLE_CUSTOMER`) trong hệ thống.
                    
                    **Lưu ý:**
                    - Email phải là duy nhất — không thể đăng ký 2 tài khoản cùng email
                    - Mật khẩu được mã hóa BCrypt — hệ thống không lưu mật khẩu thô
                    - Sau khi đăng ký thành công, dùng `POST /login` để lấy JWT
                    - Response trả về `201 Created` mà **không có body**
                    
                    **Khuyến nghị mật khẩu mạnh:** tối thiểu 8 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Đăng ký tài khoản thành công — không có body trong response. Dùng POST /login để đăng nhập."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đăng ký không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Thiếu họ tên",
                                            value = """
                                                    {
                                                      "timestamp": "2026-07-10T12:00:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "errorCode": "VALIDATION_ERROR",
                                                      "message": "Họ tên không được trống",
                                                      "path": "/api/v1/auth/register"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Email sai format",
                                            value = """
                                                    {
                                                      "timestamp": "2026-07-10T12:00:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "errorCode": "VALIDATION_ERROR",
                                                      "message": "Định dạng email không hợp lệ",
                                                      "path": "/api/v1/auth/register"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống nội bộ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
