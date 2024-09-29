package com.choice.shopping.entity;

import com.choice.product.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cart_item")
@Data
// 장바구니 아이템 엔티티
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId; // 장바구니 아이템 ID

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart; // 장바구니 ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 ID

    private Integer quantity; // 수량

    private String sessionId; // 세션 ID

    private String size;
}
