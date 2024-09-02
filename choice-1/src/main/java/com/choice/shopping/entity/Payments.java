package com.choice.shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "payments") // 테이블 이름 설정
public class Payments {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long paymentsId; // 결제 ID

    private String payValue; // 결제 값
    private Long orderId; // 주문 ID (Foreign Key)

  
}
