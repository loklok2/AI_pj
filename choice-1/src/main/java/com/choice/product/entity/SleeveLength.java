package com.choice.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "sleeve_length") // 테이블 이름 설정
public class SleeveLength {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Integer sleeveLengthId; // 소매 길이 ID

    private String sleeveLengthNameEn; // 소매 길이 이름 (영문)
    private String sleeveLengthNameKo; // 소매 길이 이름 (한글)

    // Getters and Setters (생략 가능)
}
