package com.choice.product.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_attribute_link")
@Data
// 상품 속성 링크 엔티티
public class ProductAttributeLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Long linkId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductAttribute attribute;
}