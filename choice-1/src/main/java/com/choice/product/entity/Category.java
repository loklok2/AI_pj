package com.choice.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "category") // 테이블 이름 설정
public class Category {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Integer categoryId; // 카테고리 ID

    private String categoryNameEn; // 카테고리 이름 (영문)
    private String categoryNameKo; // 카테고리 이름 (한글)

    // Getters and Setters (생략 가능)
}

