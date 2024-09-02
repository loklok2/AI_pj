package com.choice.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "token") // 테이블 이름 설정
public class Token {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long tokenId; // 토큰 ID

    private Long userId; // 사용자 ID (Foreign Key)
    private String tokenType; // 토큰 유형
    private String tokenValue; // 토큰 값

    @Column(name = "create_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 토큰 생성 일자

    @Column(name = "expired_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime expiredDate; // 토큰 만료 일자

    // Getters and Setters (생략 가능)
}
