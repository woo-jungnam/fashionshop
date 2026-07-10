package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.CouponRequestDto;
import com.thaddeus.clothing.dto.CouponResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CouponResponseDto createCoupon(CouponRequestDto request);
    CouponResponseDto getCouponById(Long id);
    CouponResponseDto getCouponByCode(String code);
    Page<CouponResponseDto> getAllCoupons(Pageable pageable);
    void deactivateCoupon(Long id);
}
