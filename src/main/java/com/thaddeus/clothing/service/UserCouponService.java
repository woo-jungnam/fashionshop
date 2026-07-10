package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.UserCouponResponseDto;

import java.util.List;

public interface UserCouponService {
    UserCouponResponseDto collectCoupon(Long userId, Long couponId);
    List<UserCouponResponseDto> getMyCoupons(Long userId, String status);
    List<UserCouponResponseDto> getUserCoupons(Long userId);
}
