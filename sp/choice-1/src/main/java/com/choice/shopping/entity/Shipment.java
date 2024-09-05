package com.choice.shopping.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment")
@Data
// 배송 엔티티
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipId; // 배송 ID

    private String shipName; // 배송 이름
    private String shipReceiveName; // 수령인 이름
    private String shipTel; // 전화번호
    private String shipZipcode; // 우편번호
    private String shipAddress; // 주소

    @Column(name = "ship_start_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime shipStartDate; // 배송 시작 날짜

    @Column(name = "ship_end_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime shipEndDate; // 배송 종료 날짜

    private String shipStatus; // 배송 상태

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order; // 주문 ID

}