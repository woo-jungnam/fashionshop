package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "static_pages",
    indexes = {
        @Index(name = "idx_static_page_slug", columnList = "slug")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StaticPage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tiêu đề trang không được trống")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Slug trang không được trống")
    @Column(nullable = false, unique = true)
    private String slug;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, INACTIVE
}
