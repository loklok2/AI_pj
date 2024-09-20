package com.choice.shopping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.entity.Cart;
import com.choice.shopping.entity.CartItem;
import com.choice.shopping.repository.CartItemRepository;
import com.choice.shopping.repository.CartRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class CartService {

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private CartItemRepository cartItemRepository;

        @Autowired
        private ProductRepository productRepository;

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

        @Transactional
        public CartItemDTO addToCart(Long userId, CartItemDTO cartItemDTO) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                Product product = productRepository.findById(cartItemDTO.getProductId())
                                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

                CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product);
                if (cartItem == null) {
                        cartItem = new CartItem();
                        cartItem.setCart(cart);
                        cartItem.setProduct(product);
                        cartItem.setQuantity(cartItemDTO.getQuantity());
                } else {
                        cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
                }

                cartItem = cartItemRepository.save(cartItem);
                return convertToCartItemDTO(cartItem);
        }

        @Transactional
        public void removeFromCart(Long userId, Long productId) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, productId);
                if (cartItem != null) {
                        cartItemRepository.delete(cartItem);
                }
        }

        @Transactional
        public CartItemDTO updateCartItem(Long userId, CartItemDTO cartItemDTO) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, cartItemDTO.getProductId());
                if (cartItem == null) {
                        throw new RuntimeException("장바구니에서 해당 상품을 찾을 수 없습니다.");
                }

                cartItem.setQuantity(cartItemDTO.getQuantity());
                cartItem = cartItemRepository.save(cartItem);
                return convertToCartItemDTO(cartItem);
        }

        private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
                Product product = cartItem.getProduct();
                String imagePath = null;
                if (!product.getImages().isEmpty()) {
                        ProductImg firstImage = product.getImages().iterator().next();
                        imagePath = "/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName();
                }
                return CartItemDTO.builder()
                                .productId(product.getProductId())
                                .pimgPath(imagePath)
                                .productName(product.getName())
                                .category(product.getCategory())
                                .quantity(cartItem.getQuantity())
                                .price(product.getPrice())
                                .totalPrice(product.getPrice() * cartItem.getQuantity())
                                .build();
        }

        public List<CartItemDTO> getCartItems(String cartItems) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                        return mapper.readValue(cartItems, new TypeReference<List<CartItemDTO>>() {
                        });
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("장바구니 아이템 파싱 중 오류가 발생했습니다.", e);
                }
        }

        public void mergeCart(Long userId, List<CartItemDTO> localCartItems) {
                for (CartItemDTO item : localCartItems) {
                        addToCart(userId, item);
                }
        }

}
