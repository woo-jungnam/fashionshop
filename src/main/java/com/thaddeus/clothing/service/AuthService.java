package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.AuthRequestDto;
import com.thaddeus.clothing.dto.AuthResponseDto;
import com.thaddeus.clothing.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto request);
    void register(RegisterRequestDto request);
}
