package com.choice.shopping.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
// 상품 엔티티
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // 상품 ID

    private String pName; // 상품 이름
    private String pInfo; // 상품 정보
    private Long pStock; // 상품 재고

    @Column(name = "p_create_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime pCreateDate; // 상품 생성 날짜

    private Long pSell; // 상품 판매량
    private Long pLike; // 상품 좋아요
    private Long pView; // 상품 조회수
}