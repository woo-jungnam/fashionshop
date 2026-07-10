package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.UserAddressRequestDto;
import com.thaddeus.clothing.dto.UserProfileResponseDto;
import com.thaddeus.clothing.dto.UserProfileUpdateRequestDto;
import com.thaddeus.clothing.entity.UserAddress;

import java.util.List;

public interface UserService {
    UserProfileResponseDto getUserProfile(Long userId);
    UserProfileResponseDto updateUserProfile(Long userId, UserProfileUpdateRequestDto request);
    List<UserAddress> getUserAddresses(Long userId);
    UserAddress addUserAddress(Long userId, UserAddressRequestDto request);
    void deleteUserAddress(Long userId, Long addressId);
}
