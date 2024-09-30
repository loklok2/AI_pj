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

        // 사용자의 장바구니 아이템 조회
        public CartSummaryDTO getCartItemsUser(Long userId) {
                log.debug("Fetching cart items for user ID: {}", userId);
                List<Object[]> results = cartItemRepository.findCartSummaryByUserId(userId);
                List<CartItemDTO> items = convertToCartItemDTOList(results);
                Long total = items.stream().mapToLong(CartItemDTO::getTotalPrice).sum();
                log.debug("Found {} cart items for user ID: {}", items.size(), userId);
                return new CartSummaryDTO(items, total);
        }

        // 사용자의 장바구니 아이템 조회
        public List<CartItem> getCartItemsForUser(Long userId) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                return cartItemRepository.findByCart(cart);
        }

        // 사용자의 장바구니 비우기
        public void clearCart(Long userId) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                cartItemRepository.deleteByCart(cart);
        }

        // 장바구니에 상품 추가
        @Transactional
        public CartItemDTO addToCart(Long userId, Long productId, Integer quantity, String size) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

                CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product);
                if (cartItem == null) {
                        cartItem = new CartItem();
                        cartItem.setCart(cart);
                        cartItem.setProduct(product);
                        cartItem.setQuantity(quantity);
                        cartItem.setSize(size);
                } else {
                        cartItem.setQuantity(cartItem.getQuantity() + quantity);
                }

                cartItem = cartItemRepository.save(cartItem);
                return convertToCartItemDTO(cartItem);
        }

        // 장바구니에서 상품 삭제
        @Transactional
        public void removeFromCart(Long userId, Long productId) {
                Cart cart = cartRepository.findByMember_UserId(userId)
                                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
                CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, productId);
                if (cartItem != null) {
                        cartItemRepository.delete(cartItem);
                }
        }

        // 장바구니에서 상품 수량 변경
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

        // 장바구니 아이템을 DTO로 변환
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
                                .size(cartItem.getSize())
                                .build();
        }

        // 결과를 CartItemDTO 리스트로 변환
        private List<CartItemDTO> convertToCartItemDTOList(List<Object[]> results) {
                // log.info("results: {}", results);
                return results.stream().map(result -> {
                        CartItemDTO dto = new CartItemDTO();
                        dto.setUserId(result[0] != null ? Long.parseLong(result[0].toString()) : null);
                        dto.setCartItemId(result[1] != null ? Long.parseLong(result[1].toString()) : null);
                        dto.setProductId(result[2] != null ? Long.parseLong(result[2].toString()) : null);
                        dto.setProductName((String) result[3]);
                        dto.setCategory((String) result[4]);
                        dto.setQuantity(result[5] != null ? Integer.parseInt(result[5].toString()) : null);
                        dto.setPrice(result[6] != null ? Long.parseLong(result[6].toString()) : null);
                        dto.setTotalPrice(result[7] != null ? Long.parseLong(result[7].toString()) : null);
                        dto.setPimgPath((String) result[8]);
                        dto.setSize((String) result[9]);
                        return dto;
                }).collect(Collectors.toList());
        }
}
