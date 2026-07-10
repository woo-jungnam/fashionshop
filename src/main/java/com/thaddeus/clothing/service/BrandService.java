package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.BrandRequestDto;
import com.thaddeus.clothing.dto.BrandResponseDto;

import java.util.List;

public interface BrandService {
    BrandResponseDto createBrand(BrandRequestDto request);
    BrandResponseDto getBrandById(Long id);
    List<BrandResponseDto> getAllBrands();
    BrandResponseDto updateBrand(Long id, BrandRequestDto request);
    void deleteBrand(Long id);
}
