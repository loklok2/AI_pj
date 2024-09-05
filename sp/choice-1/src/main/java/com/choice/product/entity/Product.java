package com.choice.product.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

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
    private Long pSell; // 상품 판매량
    private Long pLike; // 상품 좋아요
    private Long pView; // 상품 조회수

    @Column(name = "p_create_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime pCreateDate; // 상품 생성 날짜

    @ManyToMany
    @JoinTable(name = "product_attribute_link", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private Set<ProductAttribute> attributes; // 상품 속성

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImg> images; // 상품 이미지

}