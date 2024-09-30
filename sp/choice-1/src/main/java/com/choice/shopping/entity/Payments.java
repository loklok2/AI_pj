package com.choice.shopping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
// 결제 엔티티
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentsId; // 결제 ID

    @Column(name = "pay_value")
    private String payValue; // 결제 값

    @Column(name = "payment_method")
    private String paymentMethod; // 결제 방법

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order; // 주문 ID

    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // 결제 시간
}