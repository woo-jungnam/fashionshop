package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên banner không được trống")
    @Column(nullable = false)
    private String name;

    @Column(name = "desktop_image_url")
    private String desktopImageUrl;

    @Column(name = "mobile_image_url")
    private String mobileImageUrl;

    @Column(name = "click_url")
    private String clickUrl;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "position")
    private String position; // HOME_MAIN_SLIDER, HOME_SIDE_BANNER, CATEGORY_BANNER

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, INACTIVE
}
