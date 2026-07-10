package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.BrandRequestDto;
import com.thaddeus.clothing.dto.BrandResponseDto;
import com.thaddeus.clothing.entity.Brand;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.BrandRepository;
import com.thaddeus.clothing.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public BrandResponseDto createBrand(BrandRequestDto request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.BRAND_ALREADY_EXISTS);
        }

        Brand brand = Brand.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .origin(request.getOrigin())
                .build();

        Brand saved = brandRepository.save(brand);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponseDto getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        return mapToResponseDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponseDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BrandResponseDto updateBrand(Long id, BrandRequestDto request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

        if (brandRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException(ErrorCode.BRAND_ALREADY_EXISTS);
        }

        brand.setName(request.getName());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setOrigin(request.getOrigin());

        Brand updated = brandRepository.save(brand);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brandRepository.delete(brand);
    }

    private BrandResponseDto mapToResponseDto(Brand brand) {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .origin(brand.getOrigin())
                .build();
    }
}
