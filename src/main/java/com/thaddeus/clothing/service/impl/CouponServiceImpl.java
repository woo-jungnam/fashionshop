package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.CouponRequestDto;
import com.thaddeus.clothing.dto.CouponResponseDto;
import com.thaddeus.clothing.entity.Coupon;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.CouponRepository;
import com.thaddeus.clothing.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscountValue(request.getMaxDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalLimit(request.getTotalLimit())
                .userLimit(request.getUserLimit())
                .status(request.getStatus())
                .build();

        Coupon saved = couponRepository.save(coupon);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return mapToResponseDto(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getCouponByCode(String code) {
        Coupon coupon = couponRepository.findActiveCoupon(code.toUpperCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        return mapToResponseDto(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponseDto> getAllCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public void deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        coupon.setStatus("INACTIVE");
        couponRepository.save(coupon);
    }

    private CouponResponseDto mapToResponseDto(Coupon coupon) {
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscountValue(coupon.getMaxDiscountValue())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .totalLimit(coupon.getTotalLimit())
                .usedCount(coupon.getUsedCount())
                .userLimit(coupon.getUserLimit())
                .status(coupon.getStatus())
                .build();
    }
}
