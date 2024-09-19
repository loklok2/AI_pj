package com.choice.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stores")
public class Stores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long StoreId;

    @Column(name = "store_type")
    private String StoreType;

    @Column(name = "store_code")
    private String StoreCode;

    @Column(name = "store_name")
    private String StoreName;
}
