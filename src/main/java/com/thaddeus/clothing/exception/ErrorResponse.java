package com.thaddeus.clothing.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO phản hồi chi tiết lỗi từ hệ thống")
public class ErrorResponse {

    @Schema(description = "Thời điểm xảy ra lỗi", example = "2026-07-10T11:45:00")
    private LocalDateTime timestamp;

    @Schema(description = "Mã trạng thái HTTP", example = "400")
    private int status;

    @Schema(description = "Tiêu đề lỗi hoặc tên lỗi HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mã lỗi nội bộ hệ thống phục vụ mapping", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Thông báo chi tiết về nguyên nhân lỗi", example = "Họ tên không được trống")
    private String message;

    @Schema(description = "Đường dẫn API gây ra lỗi", example = "/api/v1/users/profile")
    private String path;
}
