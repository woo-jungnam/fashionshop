package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "sales_channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SalesChannel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên kênh bán hàng không được trống")
    @Column(nullable = false, unique = true)
    private String name; // WEBSITE, SHOPEE, TIKTOK_SHOP, POS_STORE

    @Column(name = "api_key")
    private String apiKey; // Token kết nối đồng bộ API sàn

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE
}
