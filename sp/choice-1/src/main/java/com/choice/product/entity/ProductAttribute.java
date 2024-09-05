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

    @ManyToMany(mappedBy = "attributes")
    private Set<Product> products; // 상품 엔티티와의 다대다 관계

}