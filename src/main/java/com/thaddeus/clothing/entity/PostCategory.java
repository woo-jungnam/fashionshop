package com.thaddeus.clothing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "post_categories",
    indexes = {
        @Index(name = "idx_post_category_slug", columnList = "slug")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PostCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên danh mục bài viết không được trống")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Slug danh mục bài viết không được trống")
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "description")
    private String description;
}
