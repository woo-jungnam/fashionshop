package com.thaddeus.clothing.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI thaddeusClothingOpenAPI() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                .externalDocs(buildExternalDocs())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(buildComponents());
    }

    private Info buildApiInfo() {
        return new Info()
                .title("Thaddeus Clothing — REST API")
                .version("1.0.0")
                .description("""
                        ## Giới thiệu
                        Đây là tài liệu API chính thức của hệ thống **Thaddeus Clothing** — nền tảng thương mại điện tử thời trang.
                        
                        ## Xác thực (Authentication)
                        Hầu hết các API yêu cầu **JWT Bearer Token**.
                        
                        **Cách lấy token:**
                        1. Gọi `POST /api/v1/auth/login` với email và mật khẩu
                        2. Lấy `accessToken` từ response
                        3. Click nút **Authorize** ở góc trên phải Swagger UI
                        4. Nhập: `Bearer <accessToken>`
                        
                        ## Cấu trúc Response Lỗi
                        Tất cả lỗi đều trả về cấu trúc thống nhất:
                        ```json
                        {
                          "timestamp": "2026-07-10T12:00:00",
                          "status": 400,
                          "error": "Bad Request",
                          "errorCode": "VALIDATION_ERROR",
                          "message": "Mô tả lỗi chi tiết",
                          "path": "/api/v1/..."
                        }
                        ```
                        
                        ## Error Codes
                        | Code | Ý nghĩa | HTTP Status |
                        |---|---|---|
                        | `VALIDATION_ERROR` | Dữ liệu đầu vào không hợp lệ | 400 |
                        | `USR_001` | Không tìm thấy người dùng | 404 |
                        | `PRD_001` | Không tìm thấy biến thể sản phẩm | 404 |
                        | `WRH_001` | Không tìm thấy kho hàng | 404 |
                        | `SHP_001` | Không tìm thấy đơn vị vận chuyển | 404 |
                        | `CPN_001` | Mã giảm giá không hợp lệ | 400 |
                        | `CPN_002` | Hết lượt sử dụng coupon | 400 |
                        | `CPN_003` | Chưa đạt giá trị đơn tối thiểu | 400 |
                        | `INV_001` | Sản phẩm hết hàng | 400 |
                        | `ORD_001` | Không tìm thấy đơn hàng | 404 |
                        | `SYS_001` | Lỗi hệ thống nội bộ | 500 |
                        
                        ## Phân trang (Pagination)
                        Các API trả về danh sách hỗ trợ phân trang qua Pageable:
                        - `?page=0` — Trang đầu tiên (bắt đầu từ 0, không phải 1)
                        - `?size=20` — Số bản ghi mỗi trang
                        - `?sort=name,asc` — Sắp xếp theo field và chiều (asc/desc)
                        """)
                .contact(new Contact()
                        .name("Thaddeus Clothing — Backend Team")
                        .email("dev@thaddeus.vn")
                        .url("https://thaddeus.vn"))
                .license(new License()
                        .name("Private — Internal Use Only")
                        .url("https://thaddeus.vn"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"),
                new Server()
                        .url("https://api.thaddeus.vn")
                        .description("Production Server")
        );
    }

    private ExternalDocumentation buildExternalDocs() {
        return new ExternalDocumentation()
                .description("Tài liệu API đầy đủ và hướng dẫn tích hợp")
                .url("https://docs.thaddeus.vn");
    }

    private Components buildComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("""
                                JWT Bearer Token để xác thực API.
                                
                                **Cách dùng:**
                                1. Gọi `POST /api/v1/auth/login` để lấy `accessToken`
                                2. Click **Authorize 🔒** → nhập: `Bearer <accessToken>`
                                3. Tất cả request sau đó tự động gắn token vào header `Authorization`
                                
                                **Token hết hạn sau:** 24 giờ (86400 giây)
                                """));
    }
}
