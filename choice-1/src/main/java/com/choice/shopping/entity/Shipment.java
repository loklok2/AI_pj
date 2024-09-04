package com.choice.shopping.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipId;

    private String shipName;
    private String shipReciveName;
    private String shipTel;
    private String shipZipcode;
    private String shipAddress;

    @Column(name = "ship_start_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime shipStartDate;

    @Column(name = "ship_end_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime shipEndDate;

    private Boolean shipStatus;

    @OneToOne(mappedBy = "shipment")
    private Invoice invoice;

    // Getters and Setters
}