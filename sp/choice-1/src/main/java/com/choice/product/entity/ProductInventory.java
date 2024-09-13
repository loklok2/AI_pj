package com.choice.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory")
@Data
public class ProductInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId; // 재고 ID

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;// 상품 엔티티와의 일대일 관계

    private Long stock; // 재고 수량

    @Enumerated(EnumType.STRING)
    private Size size; // 상품 사이즈

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated; // 마지막 업데이트 날짜

    public enum Size {
        XS, S, M, L, XL, FREE
    }
}