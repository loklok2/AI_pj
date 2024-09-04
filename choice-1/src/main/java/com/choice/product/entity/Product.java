package com.choice.product.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String pName;
    private String pInfo;
    private Long pStock;
    private Long pSell;
    private Long pLike;
    private Long pView;

    @Column(name = "p_create_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime pCreateDate;

    @ManyToMany
    @JoinTable(name = "product_attribute_link", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private Set<ProductAttribute> attributes;

    // Getters and Setters
}