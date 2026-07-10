package com.thaddeus.clothing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin webhook thanh toán từ cổng SePay. " +
        "SePay gửi POST request tới endpoint này mỗi khi có giao dịch ngân hàng mới khớp với tài khoản cấu hình.")
public class SePayWebhookDto {

    @Schema(description = "ID giao dịch nội bộ của SePay.", example = "123456789")
    private Long id;

    @Schema(description = "Tên cổng/ngân hàng nhận giao dịch.", example = "VietcomBank")
    private String gateway;

    @Schema(description = "Ngày giờ giao dịch theo múi giờ của ngân hàng (format: dd/MM/yyyy HH:mm:ss).", example = "10/07/2026 14:30:00")
    private String transactionDate;

    @Schema(description = "Số tài khoản ngân hàng nhận tiền.", example = "1234567890")
    private String accountNumber;

    @Schema(description = "Tài khoản phụ (nếu có).", example = "null", nullable = true)
    private String subAccount;

    @Schema(
            description = "Số tiền chuyển khoản thực tế (VND). " +
                    "Hệ thống so sánh giá trị này với totalAmount của đơn hàng để xác nhận thanh toán đủ tiền.",
            example = "548000"
    )
    private BigDecimal transferAmount;

    @Schema(description = "Loại giao dịch: 'in' = nhận tiền, 'out' = chuyển tiền đi.", example = "in", allowableValues = {"in", "out"})
    private String transferType;

    @Schema(
            description = "Mã code do SePay sinh ra để đối chiếu giao dịch — có thể chứa mã đơn hàng dạng ORD-XXXXXX.",
            example = "ORD-A1B2C3",
            nullable = true
    )
    private String code;

    @Schema(
            description = "Nội dung chuyển khoản thực tế do người dùng nhập khi chuyển tiền. " +
                    "Hệ thống sẽ tìm chuỗi 'ORD-XXXXXX' trong nội dung này để xác định đơn hàng cần xác nhận thanh toán. " +
                    "Ví dụ: 'Thanh toan don hang ORD-A1B2C3' → hệ thống bóc ra 'ORD-A1B2C3'.",
            example = "Thanh toan don hang ORD-A1B2C3"
    )
    private String content;

    @Schema(description = "Mã tham chiếu giao dịch từ phía ngân hàng.", example = "FT26191ABC1234", nullable = true)
    private String referenceCode;
}
