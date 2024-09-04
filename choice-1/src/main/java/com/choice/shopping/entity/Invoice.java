package com.choice.shopping.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    private Long invoiceId;

    private String invoiceCode;
    private String invoiceCompany;

    @Column(name = "invoice_create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime invoiceCreateDate;

    @Column(name = "invoice_edited_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime invoiceEditedDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "invoice_id")
    private Shipment shipment;

    // Getters and Setters
}