package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.SettingRequestDto;
import com.thaddeus.clothing.dto.SettingResponseDto;

import java.util.List;

public interface SettingService {
    SettingResponseDto saveSetting(SettingRequestDto request);
    SettingResponseDto getSettingByKey(String key);
    List<SettingResponseDto> getAllSettings();
}
