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

    private String name; // 상품 이름
    private String info; // 상품 정보

    @Enumerated(EnumType.STRING)
    private Size size; // 상품 사이즈

    private Long stock; // 상품 재고
    private Long sell; // 상품 판매량

    @Column(name = "price", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long price; // 상품 가격 (원 단위)

    @Column(name = "create_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 상품 생성 날짜

    @Column(name = "like")
    private Long likeCount; // 상품 좋아요

    private Long view; // 상품 조회수

    @ManyToMany
    @JoinTable(name = "product_attribute_link", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private Set<ProductAttribute> attributes; // 상품 속성

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImg> images; // 상품 이미지

    public enum Size {
        XS, S, M, L, XL, FREE
    }
}