package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.AuthRequestDto;
import com.thaddeus.clothing.dto.AuthResponseDto;
import com.thaddeus.clothing.dto.RegisterRequestDto;
import com.thaddeus.clothing.entity.Role;
import com.thaddeus.clothing.entity.User;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.RoleRepository;
import com.thaddeus.clothing.repository.UserRepository;
import com.thaddeus.clothing.security.JwtTokenProvider;
import com.thaddeus.clothing.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AuthResponseDto.builder()
                .id(user.getId())
                .accessToken(jwt)
                .tokenType("Bearer")
                .email(request.getEmail())
                .roles(authentication.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build()));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .salt(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .status("ACTIVE")
                .build();

        user.getRoles().add(userRole);
        userRepository.save(user);
    }
}
