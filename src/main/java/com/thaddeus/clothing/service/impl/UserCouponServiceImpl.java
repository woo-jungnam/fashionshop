package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.UserCouponResponseDto;
import com.thaddeus.clothing.entity.Coupon;
import com.thaddeus.clothing.entity.User;
import com.thaddeus.clothing.entity.UserCoupon;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.CouponRepository;
import com.thaddeus.clothing.repository.UserCouponRepository;
import com.thaddeus.clothing.repository.UserCouponWalletRepository;
import com.thaddeus.clothing.repository.UserRepository;
import com.thaddeus.clothing.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserCouponWalletRepository walletRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserCouponResponseDto collectCoupon(Long userId, Long couponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (userCouponRepository.findByUserIdAndCouponId(userId, couponId).isPresent()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .savedAt(LocalDateTime.now())
                .usageCount(0)
                .status("UNUSED")
                .build();

        UserCoupon saved = userCouponRepository.save(userCoupon);
        return UserCouponResponseDto.builder()
                .id(saved.getId())
                .couponId(saved.getCoupon().getId())
                .code(saved.getCoupon().getCode())
                .discountType(saved.getCoupon().getDiscountType())
                .discountValue(saved.getCoupon().getDiscountValue())
                .minOrderValue(saved.getCoupon().getMinOrderValue())
                .expiryDate(saved.getCoupon().getEndDate())
                .status(saved.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCouponResponseDto> getMyCoupons(Long userId, String status) {
        List<UserCoupon> list = (status == null || status.isBlank())
                ? walletRepository.findByUserId(userId)
                : walletRepository.findByUserIdAndStatus(userId, status.toUpperCase());

        return list.stream()
                .map(uc -> UserCouponResponseDto.builder()
                        .id(uc.getId())
                        .couponId(uc.getCoupon().getId())
                        .code(uc.getCoupon().getCode())
                        .discountType(uc.getCoupon().getDiscountType())
                        .discountValue(uc.getCoupon().getDiscountValue())
                        .minOrderValue(uc.getCoupon().getMinOrderValue())
                        .expiryDate(uc.getCoupon().getEndDate())
                        .status(uc.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCouponResponseDto> getUserCoupons(Long userId) {
        return getMyCoupons(userId, null);
    }
}
