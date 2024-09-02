package com.choice.shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "cart") // 테이블 이름 설정
public class Cart {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long cartId; // 장바구니 ID

    private Long userId; // 사용자 ID (Foreign Key)
    private Long productId; // 제품 ID (Foreign Key)
    private Long cartQty; // 장바구니 수량

  
}
