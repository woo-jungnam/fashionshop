package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.PostResponseDto;
import com.thaddeus.clothing.entity.Post;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.PostRepository;
import com.thaddeus.clothing.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getActivePosts(Pageable pageable) {
        return postRepository.findByStatus("PUBLISHED", pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getPostBySlug(String slug) {
        Post post = postRepository.findBySlugAndStatus(slug, "PUBLISHED")
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return mapToResponseDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return mapToResponseDto(post);
    }

    private PostResponseDto mapToResponseDto(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .thumbnailUrl(post.getThumbnailUrl())
                .shortDescription(post.getShortDescription())
                .content(post.getContent())
                .author(post.getAuthor())
                .categoryName(post.getCategory().getName())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
