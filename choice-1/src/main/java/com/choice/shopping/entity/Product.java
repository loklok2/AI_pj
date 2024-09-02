package com.choice.shopping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "product") // 테이블 이름 설정
public class Product {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long productId; // 제품 ID

    private String pName; // 제품 이름
    private String pInfo; // 제품 정보
    private Long pStock; // 제품 재고 수량
    private Long pSell; // 판매 수량
    private Long pLike; // 좋아요 수
    private Long pView; // 조회 수

    @Column(name = "p_create_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime pCreateDate; // 제품 생성 일자

    // Foreign key 참조
    private Integer categoryId; // 카테고리 ID
    private Integer colorId; // 색상 ID
    private Integer detailId; // 상세 ID
    private Integer fitId; // 핏 ID
    private Integer karaId; // 카라 ID
    private Integer materialId; // 소재 ID
    private Integer necklineId; // 넥라인 ID
    private Integer printId; // 프린트 ID
    private Integer sleeveLengthId; // 소매 길이 ID

    
}
