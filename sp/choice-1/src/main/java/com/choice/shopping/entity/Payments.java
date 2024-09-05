package com.choice.shopping.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
// 결제 엔티티
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentsId; // 결제 ID

    private String payValue; // 결제 값

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // 주문 ID

}