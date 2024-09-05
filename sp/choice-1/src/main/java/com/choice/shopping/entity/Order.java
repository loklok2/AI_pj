package com.choice.shopping.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.choice.auth.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
// 주문 엔티티
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // 주문 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member; // 사용자 ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 ID

    private BigDecimal totalPrice; // 총 가격
    private String orderComment; // 주문 코멘트
    private String receiveName; // 수령인 이름
    private String address; // 주소
    private String tel; // 전화번호

    @Column(name = "order_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderDate; // 주문 날짜

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    public enum OrderStatus { // 주문 상태
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
}