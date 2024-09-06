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

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate; // 배송 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status")
    private ShippingStatus shippingStatus; // 배송 상태

    @Column(name = "invoice_code")
    private String invoiceCode; // 송장 번호

    @Column(name = "courier_company")
    private String courierCompany; // 택배 회사

    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders order; // 주문 ID

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private ShippingAddress shippingAddress; // 배송 주소 ID

    @Column(name = "invoice_created_at")
    private LocalDateTime invoiceCreatedAt; // 송장 생성 날짜

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated; // 마지막 업데이트 날짜

    public enum ShippingStatus {
        PENDING, // 대기
        SHIPPED, // 배송 중
        DELIVERED, // 배송 완료
        CANCELLED // 취소
    }
}