package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostResponseDto> getActivePosts(Pageable pageable);
    PostResponseDto getPostBySlug(String slug);
    Page<PostResponseDto> getAllPosts(Pageable pageable);
    PostResponseDto getPostById(Long id);
}
