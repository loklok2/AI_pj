package com.choice.shopping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "morder") // 테이블 이름 설정
public class Morder {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long orderId; // 주문 ID

    private Long userId; // 사용자 ID (Foreign Key)
    private Long productId; // 제품 ID (Foreign Key)
    private Long totalPrice; // 총 가격
    private String orderComment; // 주문 코멘트
    private String receiveName; // 수령자 이름
    private String address; // 수령 주소
    private String tel; // 연락처

    @Column(name = "order_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderDate; // 주문 날짜

    // Getters and Setters (생략 가능)
}
