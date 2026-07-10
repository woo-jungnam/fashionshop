package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.SettingRequestDto;
import com.thaddeus.clothing.dto.SettingResponseDto;
import com.thaddeus.clothing.entity.SystemSetting;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.SystemSettingRepository;
import com.thaddeus.clothing.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final SystemSettingRepository systemSettingRepository;

    @Override
    @Transactional
    public SettingResponseDto saveSetting(SettingRequestDto request) {
        Optional<SystemSetting> optional = systemSettingRepository.findByKey(request.getKey());
        SystemSetting setting;

        if (optional.isPresent()) {
            setting = optional.get();
            setting.setValue(request.getValue());
            setting.setDescription(request.getDescription());
        } else {
            setting = SystemSetting.builder()
                    .key(request.getKey())
                    .value(request.getValue())
                    .description(request.getDescription())
                    .build();
        }

        SystemSetting saved = systemSettingRepository.save(setting);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingResponseDto getSettingByKey(String key) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
        return mapToResponseDto(setting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingResponseDto> getAllSettings() {
        return systemSettingRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private SettingResponseDto mapToResponseDto(SystemSetting setting) {
        return SettingResponseDto.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .description(setting.getDescription())
                .build();
    }
}
