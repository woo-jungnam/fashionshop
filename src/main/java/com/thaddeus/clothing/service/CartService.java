package com.thaddeus.clothing.service;

import com.thaddeus.clothing.dto.CartItemRequestDto;
import com.thaddeus.clothing.dto.CartResponseDto;

public interface CartService {
    CartResponseDto getCart(Long userId);
    CartResponseDto addItemToCart(Long userId, CartItemRequestDto request);
    CartResponseDto updateCartItem(Long userId, Long cartItemId, Integer quantity);
    CartResponseDto removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
