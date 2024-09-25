package com.choice.product.entity;

import java.time.LocalDateTime;
import java.util.Objects;
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

    private Long sell; // 상품 판매량

    private String category; // 상품 카테고리

    @Column(name = "price", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long price; // 상품 가격 (원 단위)

    @Column(name = "create_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 상품 생성 날짜

    @Column(name = "like_count")
    private Long likeCount; // 상품 좋아요

    private Long view; // 상품 조회수

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductAttributeLink> attributeLinks; // 상품 속성 링크 엔티티와의 일대다 관계

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImg> images; // 상품 이미지

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Product))
            return false;
        Product product = (Product) o;
        return Objects.equals(getProductId(), product.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId());
    }

}