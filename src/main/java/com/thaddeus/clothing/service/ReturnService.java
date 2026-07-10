package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.ReturnRequestDto;
import com.thaddeus.clothing.dto.ReturnResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReturnService {
    ReturnResponseDto createReturnRequest(Long userId, ReturnRequestDto request);
    ReturnResponseDto getReturnById(Long id);
    Page<ReturnResponseDto> getReturnsByUser(Long userId, Pageable pageable);
    ReturnResponseDto updateReturnStatus(Long id, String status);
}
