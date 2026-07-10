package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "system_settings",
    indexes = {
        @Index(name = "idx_setting_key", columnList = "setting_key")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SystemSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Key cấu hình không được trống")
    @Column(name = "setting_key", nullable = false, unique = true)
    private String key; // logo_url, hotline, email_support, header_links, footer_text

    @Lob
    @Column(name = "setting_value", columnDefinition = "LONGTEXT")
    private String value; // Giá trị cấu hình (có thể lưu text hoặc JSON object cấu trúc phức tạp)

    @Column(name = "description")
    private String description;
}
