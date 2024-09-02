package com.choice.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "material") // 테이블 이름 설정
public class Material {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Integer materialId; // 소재 ID

    private String materialNameEn; // 소재 이름 (영문)
    private String materialNameKo; // 소재 이름 (한글)

   
}
