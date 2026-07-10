package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.ProductImageResponseDto;
import com.thaddeus.clothing.dto.ProductVariantResponseDto;
import com.thaddeus.clothing.dto.SizeGuideResponseDto;
import com.thaddeus.clothing.entity.Product;
import com.thaddeus.clothing.entity.SizeGuide;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.ProductImageRepository;
import com.thaddeus.clothing.repository.ProductRepository;
import com.thaddeus.clothing.repository.ProductVariantRepository;
import com.thaddeus.clothing.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponseDto> getProductVariants(Long productId) {
        return variantRepository.findByProductId(productId).stream()
                .map(v -> ProductVariantResponseDto.builder()
                        .id(v.getId())
                        .sku(v.getSku())
                        .barcode(v.getBarcode())
                        .price(v.getPrice())
                        .salePrice(v.getSalePrice())
                        .status(v.getStatus())
                        .attributes(v.getAttributeValues().stream()
                                .map(av -> av.getAttribute().getName() + ": " + av.getValue())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImages(Long productId) {
        return imageRepository.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(img -> ProductImageResponseDto.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .isMain(img.isMain())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SizeGuideResponseDto getSizeGuide(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        SizeGuide guide = product.getSizeGuide();
        if (guide == null) {
            return null;
        }

        return SizeGuideResponseDto.builder()
                .id(guide.getId())
                .name(guide.getName())
                .imageUrl(guide.getImageUrl())
                .specifications(guide.getSpecifications())
                .build();
    }
}
