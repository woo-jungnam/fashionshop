package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatus(String status, Pageable pageable);
    Optional<Post> findBySlugAndStatus(String slug, String status);
}
