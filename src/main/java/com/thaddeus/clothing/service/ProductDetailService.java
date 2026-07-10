package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.ProductImageResponseDto;
import com.thaddeus.clothing.dto.ProductVariantResponseDto;
import com.thaddeus.clothing.dto.SizeGuideResponseDto;

import java.util.List;

public interface ProductDetailService {
    List<ProductVariantResponseDto> getProductVariants(Long productId);
    List<ProductImageResponseDto> getProductImages(Long productId);
    SizeGuideResponseDto getSizeGuide(Long productId);
}
