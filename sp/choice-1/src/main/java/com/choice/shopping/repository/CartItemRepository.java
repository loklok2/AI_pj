package com.choice.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.product.entity.Product;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.entity.Cart;
import com.choice.shopping.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 장바구니와 상품으로 장바구니 아이템 찾기
    CartItem findByCartAndProduct(Cart cart, Product product);

    // 장바구니로 장바구니 아이템 찾기
    List<CartItem> findByCart(Cart cart);

    // 장바구니 아이템 삭제
    void deleteByCart(Cart cart);

    CartItem findByCartAndProduct_ProductId(Cart cart, Long productId);

    @Query(nativeQuery = true, value = "SELECT * FROM cart_summary_view WHERE user_id = :userId")
    List<CartItemDTO> findCartSummaryByUserId(@Param("userId") Long userId);

}