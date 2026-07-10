package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.ReviewRequestDto;
import com.thaddeus.clothing.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponseDto submitReview(Long userId, ReviewRequestDto request);
    Page<ReviewResponseDto> getProductReviews(Long productId, Pageable pageable);
    ReviewResponseDto approveReview(Long reviewId);
}
