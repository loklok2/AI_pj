package com.choice.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "product_attributes")
@Data
// 상품 속성 엔티티
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attributeId; // 속성 ID

    @Column(nullable = false)
    private String attributeType; // 속성 유형

    private String nameKo; // 속성 이름

    @OneToMany(mappedBy = "attribute")
    private Set<ProductAttributeLink> productLinks; // 상품 속성 링크 엔티티와의 일대다 관계

}