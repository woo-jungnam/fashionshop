package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kết quả đăng nhập thành công — chứa JWT Access Token và thông tin tài khoản")
public class AuthResponseDto {

    @Schema(
            description = "JWT Access Token dùng để xác thực cho tất cả API yêu cầu đăng nhập. " +
                    "Gắn vào header 'Authorization: Bearer <accessToken>' cho mọi request tiếp theo. " +
                    "Token có hiệu lực trong 24 giờ (86400 giây).",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0aGFkZGV1cy52biIsImlhdCI6MTc1MjE0OTAwMCwiZXhwIjoxNzUyMjM1NDAwfQ.abc123xyz",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String accessToken;

    @Schema(
            description = "Loại token — luôn là 'Bearer'. Dùng làm prefix khi gắn vào header Authorization.",
            example = "Bearer",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String tokenType;

    @Schema(
            description = "Địa chỉ email của tài khoản vừa đăng nhập.",
            example = "admin@thaddeus.vn",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String email;

    @Schema(
            description = "Danh sách các vai trò (roles) được gán cho tài khoản. " +
                    "Frontend dùng để điều hướng UI theo quyền: ROLE_ADMIN có toàn quyền, ROLE_CUSTOMER chỉ mua hàng.",
            example = "[\"ROLE_ADMIN\"]",
            accessMode = Schema.AccessMode.READ_ONLY,
            allowableValues = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_STAFF"}
    )
    private Set<String> roles;
}
