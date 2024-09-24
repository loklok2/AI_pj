package com.choice.shopping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.entity.Cart;
import com.choice.shopping.entity.CartItem;
import com.choice.shopping.repository.CartItemRepository;
import com.choice.shopping.repository.CartRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
@Service
public class CartService {

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private CartItemRepository cartItemRepository;

        @Autowired
        private ProductRepository productRepository;

        public CartSummaryDTO getCartItemsUser(Long userId) {
                log.debug("Fetching cart items for user ID: {}", userId);
                List<Object[]> results = cartItemRepository.findCartSummaryByUserId(userId);
                List<CartItemDTO> items = convertToCartItemDTOList(results);
                Long total = items.isEmpty() ? 0L : ((Number) results.get(0)[8]).longValue();
                log.debug("Found {} cart items for user ID: {}", items.size(), userId);
                return new CartSummaryDTO(items, total);
        }

        public CartSummaryDTO getCartItemsSession(String sessionId) {
                List<Object[]> results = cartItemRepository.findCartSummaryBySessionId(sessionId);
                List<CartItemDTO> items = convertToCartItemDTOList(results);
                Long total = items.isEmpty() ? 0L : ((Number) results.get(0)[8]).longValue();
                return new CartSummaryDTO(items, total);
        }

        public Long getCartTotal(Long userId) {
                List<Object[]> results = cartItemRepository.findCartSummaryByUserId(userId);
                return results.isEmpty() ? 0L : ((Number) results.get(0)[8]).longValue();
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
                                .productName(product.getName())
                                .category(product.getCategory())
                                .quantity(cartItem.getQuantity())
                                .price(product.getPrice())
                                .totalPrice(product.getPrice() * cartItem.getQuantity())
                                .pimgPath(imagePath) // pimgPath 추가
                                .build();
        }

        public List<Object[]> getCartItems(String sessionId) {
                // 세션 ID를 사용하여 비로그인 사용자의 장바구니 아이템 조회
                return cartItemRepository.findCartSummaryBySessionId(sessionId);
        }

        public void mergeCart(Long userId, String sessionId) {
                List<Object[]> sessionCartItems = getCartItems(sessionId);
                List<CartItemDTO> cartItemDTOs = convertToCartItemDTOList(sessionCartItems);
                for (CartItemDTO cartItemDTO : cartItemDTOs) {
                        addToCart(userId, cartItemDTO);
                }
                // 세션 장바구니 비우기
                clearSessionCart(sessionId);
        }

        private void clearSessionCart(String sessionId) {
                // 세션 ID에 해당하는 장바구니 아이템 삭제
                cartItemRepository.deleteBySessionId(sessionId);
        }

        private List<CartItemDTO> convertToCartItemDTOList(List<Object[]> results) {
                return results.stream().map(result -> {
                        CartItemDTO dto = new CartItemDTO();
                        dto.setUserId(result[0] != null ? ((Number) result[0]).longValue() : null);
                        dto.setSessionId((String) result[1]);
                        dto.setCartItemId(((Number) result[2]).longValue());
                        dto.setProductId(((Number) result[3]).longValue());
                        dto.setProductName((String) result[4]);
                        dto.setCategory((String) result[5]);
                        dto.setQuantity(((Number) result[6]).intValue());
                        dto.setPrice(((Number) result[7]).longValue());
                        dto.setTotalPrice(((Number) result[8]).longValue());
                        dto.setPimgPath((String) result[9]); // pimgPath 추가
                        return dto;
                }).collect(Collectors.toList());
        }

}
