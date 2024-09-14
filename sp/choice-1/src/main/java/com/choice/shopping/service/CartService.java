package com.choice.shopping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
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
                return cartItemRepository.findCartSummaryByUserId(userId);
        }

        public Long getCartTotal(Long userId) {
                return cartItemRepository.findCartSummaryByUserId(userId)
                        .stream()
                        .mapToLong(CartItemDTO::getTotalPrice)
                        .sum();
        }


        public CartSummaryDTO getCartSummary(Long userId) {
                List<CartItemDTO> items = getCartItems(userId);
                Long total = getCartTotal(userId);

                CartSummaryDTO summary = new CartSummaryDTO();
                summary.setItems(items);
                summary.setTotal(total);

                return summary;
        }

        public List<CartItem> getCartItemsForUser(Long userId) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                return cartItemRepository.findByCart(cart);
        }

        public void clearCart(Long userId) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                cartItemRepository.deleteByCart(cart);
        }
}
