package com.choice.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "product_img") // 테이블 이름 설정
public class ProductImg {
	

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long pimgId; // 제품 이미지 ID

    private Long productId; // 제품 ID (Foreign Key)
    private String pimgName; // 이미지 이름
    private byte[] pimgData; // 이미지 데이터

    // Getters and Setters (생략 가능)
}
