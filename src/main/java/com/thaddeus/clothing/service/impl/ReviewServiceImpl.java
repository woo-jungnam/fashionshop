package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.ReviewRequestDto;
import com.thaddeus.clothing.dto.ReviewResponseDto;
import com.thaddeus.clothing.entity.OrderItem;
import com.thaddeus.clothing.entity.Product;
import com.thaddeus.clothing.entity.Review;
import com.thaddeus.clothing.entity.User;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.OrderItemRepository;
import com.thaddeus.clothing.repository.ProductRepository;
import com.thaddeus.clothing.repository.ReviewRepository;
import com.thaddeus.clothing.repository.UserRepository;
import com.thaddeus.clothing.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public ReviewResponseDto submitReview(Long userId, ReviewRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        if (reviewRepository.existsByOrderItemId(request.getOrderItemId())) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .orderItem(orderItem)
                .rating(request.getRating())
                .comment(request.getComment())
                .mediaUrls(request.getMediaUrls())
                .status("PENDING")
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndStatus(productId, "APPROVED", pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public ReviewResponseDto approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        review.setStatus("APPROVED");
        Review saved = reviewRepository.save(review);
        return mapToResponseDto(saved);
    }

    private ReviewResponseDto mapToResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .mediaUrls(review.getMediaUrls())
                .status(review.getStatus())
                .build();
    }
}
