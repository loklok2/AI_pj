package com.choice.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "fit") // 테이블 이름 설정
public class Fit {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Integer fitId; // 핏 ID

    private String fitNameEn; // 핏 이름 (영문)
    private String fitNameKo; // 핏 이름 (한글)

   
}
