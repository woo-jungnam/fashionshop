package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tạo mới hoặc cập nhật sản phẩm thời trang")
public class ProductRequestDto {

    @NotBlank(message = "Tên sản phẩm không được trống")
    @Schema(
            description = "Tên hiển thị của sản phẩm trên website và ứng dụng. " +
                    "Không được để trống, không chỉ chứa khoảng trắng.",
            example = "Áo Polo Nam Cổ Bẻ Basic Premium",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @NotBlank(message = "Mã SKU gốc không được trống")
    @Schema(
            description = "Mã SKU cha (Parent SKU) — định danh nội bộ duy nhất cho nhóm sản phẩm. " +
                    "Mỗi biến thể (size/màu) sẽ có SKU riêng theo quy tắc: {parentSku}-{COLOR}-{SIZE}. " +
                    "Ví dụ: từ 'POLO-001' sinh ra 'POLO-001-RED-M', 'POLO-001-RED-L'.",
            example = "POLO-BASIC-PREM-001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String parentSku;

    @NotBlank(message = "Slug không được trống")
    @Schema(
            description = "Đường dẫn URL thân thiện SEO — chỉ chữ thường, số và dấu gạch ngang. " +
                    "Không dùng dấu tiếng Việt, khoảng trắng hay ký tự đặc biệt. " +
                    "Phải là duy nhất trong toàn hệ thống. " +
                    "Ví dụ xấu: 'Áo Polo Nam' → Ví dụ đúng: 'ao-polo-nam'.",
            example = "ao-polo-nam-co-be-basic-premium",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String slug;

    @Schema(
            description = "Mô tả ngắn hiển thị trong danh sách sản phẩm và kết quả tìm kiếm (tối đa ~500 ký tự). " +
                    "Không cần HTML, chỉ là văn bản thuần.",
            example = "Áo Polo nam chất liệu cotton Pima 100%, co giãn 4 chiều, form Regular Fit — phù hợp công sở và dạo phố.",
            nullable = true
    )
    private String shortDescription;

    @Schema(
            description = "Mô tả chi tiết đầy đủ của sản phẩm, hỗ trợ HTML. " +
                    "Hiển thị trong trang chi tiết sản phẩm. Có thể chứa bảng, danh sách, heading.",
            example = "<p>Áo Polo nam phiên bản Premium với chất liệu <strong>cotton Pima</strong> cao cấp nhập khẩu...</p>",
            nullable = true
    )
    private String description;

    @Schema(
            description = "Thành phần chất liệu vải/vật liệu sản phẩm. Quan trọng với khách hàng có da nhạy cảm.",
            example = "100% Cotton Pima",
            nullable = true
    )
    private String material;

    @Schema(
            description = "Hướng dẫn giặt và bảo quản sản phẩm để giữ độ bền và màu sắc.",
            example = "Giặt máy ≤30°C, không tẩy, phơi bóng mát, ủi nhiệt thấp mặt trái",
            nullable = true
    )
    private String careInstructions;

    @NotNull(message = "Mã danh mục không được trống")
    @Schema(
            description = "ID danh mục sản phẩm (bắt buộc). Lấy danh sách ID hợp lệ từ GET /api/v1/categories. " +
                    "Ví dụ: 1 = Áo Nam, 2 = Quần Nữ, 3 = Phụ kiện.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoryId;

    @Schema(
            description = "ID thương hiệu (tuỳ chọn). Để null nếu là hàng no-brand. " +
                    "Ví dụ: 1 = Canifa, 2 = Routine, 3 = Aristino.",
            example = "1",
            nullable = true
    )
    private Long brandId;

    @NotBlank(message = "Trạng thái không được trống")
    @Schema(
            description = "Trạng thái hiển thị của sản phẩm trên hệ thống:\n" +
                    "- `ACTIVE`: Đang bán, hiển thị trên website\n" +
                    "- `INACTIVE`: Ngừng bán, ẩn khỏi website\n" +
                    "- `DRAFT`: Đang soạn thảo, chỉ Admin thấy",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "DRAFT"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;
}
