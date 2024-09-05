package com.choice.shopping.entity;

import com.choice.auth.entity.Member;
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
@Table(name = "cart")
@Data
// 장바구니 엔티티
public class Cart {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long cartId; // 장바구니 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member; // 사용자 ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 ID

    private Integer cartQty; // 장바구니 수량
}
