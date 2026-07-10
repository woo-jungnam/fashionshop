package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.UserAddressRequestDto;
import com.thaddeus.clothing.dto.UserProfileResponseDto;
import com.thaddeus.clothing.dto.UserProfileUpdateRequestDto;
import com.thaddeus.clothing.entity.User;
import com.thaddeus.clothing.entity.UserAddress;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.UserAddressRepository;
import com.thaddeus.clothing.repository.UserRepository;
import com.thaddeus.clothing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateUserProfile(Long userId, UserProfileUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setAvatarUrl(request.getAvatarUrl());

        User updated = userRepository.save(user);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserAddress addUserAddress(Long userId, UserAddressRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.isDefault()) {
            userAddressRepository.resetDefaultAddress(userId);
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .detailAddress(request.getDetailAddress())
                .ward(request.getWard())
                .district(request.getDistrict())
                .province(request.getProvince())
                .addressType(request.getAddressType())
                .isDefault(request.isDefault())
                .build();

        return userAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteUserAddress(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        userAddressRepository.delete(address);
    }

    private UserProfileResponseDto mapToResponseDto(User user) {
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .dob(user.getDob())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()))
                .build();
    }
}
