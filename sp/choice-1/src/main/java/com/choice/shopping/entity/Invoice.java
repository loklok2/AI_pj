package com.choice.shopping.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Data
// 송장 엔티티
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId; // 송장 ID

    private String invoiceCode; // 송장 코드
    private String invoiceCompany; // 송장 회사

    @Column(name = "invoice_create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime invoiceCreateDate; // 송장 생성 날짜

    @Column(name = "invoice_edited_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime invoiceEditedDate; // 송장 수정 날짜

    @OneToOne
    @JoinColumn(name = "ship_id")
    private Shipment shipment; // 배송 ID
}