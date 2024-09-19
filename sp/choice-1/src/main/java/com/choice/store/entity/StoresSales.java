package com.choice.store.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stores_sales")
public class StoresSales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_date")
    private Date saleDate;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Stores store;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private StoresProduct product;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private StoresColors color;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private StoresSizes size;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "price")
    private Long price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private StoreCategory category;

}
