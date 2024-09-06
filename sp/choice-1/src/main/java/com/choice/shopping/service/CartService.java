package com.choice.shopping.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choice.product.entity.Product;
import com.choice.product.entity.ProductAttribute;
import com.choice.product.entity.ProductImg;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.entity.Cart;
import com.choice.shopping.entity.CartItem;
import com.choice.shopping.repository.CartItemRepository;
import com.choice.shopping.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<CartItemDTO> getCartItems(Long userId) {
        Cart cart = cartRepository.findByMember_UserId(userId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        return cartItemRepository.findByCart(cart).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getCartTotal(Long userId) {
        Cart cart = cartRepository.findByMember_UserId(userId)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        return cartItemRepository.findByCart(cart).stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        Product product = cartItem.getProduct();
        String imageUrl = product.getImages().stream()
                .findFirst()
                .map(ProductImg::getPimgName)
                .orElse(null);

        String category = product.getAttributes().stream()
                .filter(attr -> "category".equals(attr.getAttributeType()))
                .findFirst()
                .map(ProductAttribute::getNameKo)
                .orElse(null);

        return new CartItemDTO(
                product.getProductId(),
                imageUrl,
                product.getName(),
                category,
                cartItem.getQuantity(),
                product.getPrice(),
                product.getPrice() * cartItem.getQuantity());
    }
}
