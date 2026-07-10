package com.thaddeus.clothing.service.impl;

import com.thaddeus.clothing.dto.CartItemRequestDto;
import com.thaddeus.clothing.dto.CartItemResponseDto;
import com.thaddeus.clothing.dto.CartResponseDto;
import com.thaddeus.clothing.entity.Cart;
import com.thaddeus.clothing.entity.CartItem;
import com.thaddeus.clothing.entity.ProductVariant;
import com.thaddeus.clothing.entity.User;
import com.thaddeus.clothing.exception.BusinessException;
import com.thaddeus.clothing.exception.ErrorCode;
import com.thaddeus.clothing.repository.CartItemRepository;
import com.thaddeus.clothing.repository.CartRepository;
import com.thaddeus.clothing.repository.ProductVariantRepository;
import com.thaddeus.clothing.repository.UserRepository;
import com.thaddeus.clothing.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartResponseDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto addItemToCart(Long userId, CartItemRequestDto request) {
        Cart cart = getOrCreateCart(userId);
        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        Optional<CartItem> optionalItem = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), variant.getId());

        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.addCartItem(item);
            cartItemRepository.save(item);
        }

        return mapToResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return mapToResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);
        return mapToResponseDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    private CartResponseDto mapToResponseDto(Cart cart) {
        return CartResponseDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(cart.getCartItems().stream()
                        .map(item -> CartItemResponseDto.builder()
                                .id(item.getId())
                                .productVariantId(item.getProductVariant().getId())
                                .sku(item.getProductVariant().getSku())
                                .productName(item.getProductVariant().getProduct().getName())
                                .price(item.getProductVariant().getPrice())
                                .salePrice(item.getProductVariant().getSalePrice())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
