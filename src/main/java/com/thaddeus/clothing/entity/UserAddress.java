package com.thaddeus.clothing.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Thực thể địa chỉ giao hàng của người dùng")
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID duy nhất của địa chỉ", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(hidden = true)
    private User user;

    @NotBlank(message = "Tên người nhận không được trống")
    @Column(name = "recipient_name", nullable = false)
    @Schema(description = "Tên người nhận", example = "Nguyễn Văn A")
    private String recipientName;

    @NotBlank(message = "Số điện thoại nhận không được trống")
    @Column(name = "recipient_phone", nullable = false)
    @Schema(description = "Số điện thoại người nhận", example = "0987654321")
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ chi tiết không được trống")
    @Column(name = "detail_address", nullable = false)
    @Schema(description = "Số nhà, ngõ ngách, tên đường", example = "Số 123 Đường Láng")
    private String detailAddress;

    @NotBlank(message = "Phường/Xã không được trống")
    @Column(nullable = false)
    @Schema(description = "Phường/Xã", example = "Láng Hạ")
    private String ward;

    @NotBlank(message = "Quận/Huyện không được trống")
    @Column(nullable = false)
    @Schema(description = "Quận/Huyện", example = "Đống Đa")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được trống")
    @Column(nullable = false)
    @Schema(description = "Tỉnh/Thành phố", example = "Hà Nội")
    private String province;

    @Column(name = "address_type")
    @Schema(description = "Loại địa chỉ", example = "HOME", allowableValues = {"HOME", "OFFICE"})
    private String addressType; // HOME, OFFICE

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    @Schema(description = "Là địa chỉ giao nhận mặc định hay không", example = "true")
    private boolean isDefault = false;
}
