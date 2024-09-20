package com.choice.store.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;

@Entity
@Table(name = "stores_sales")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "getTopSellingProducts", procedureName = "get_top_selling_products", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_year", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_month", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_day", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_store_id", type = Long.class)
        }),
        @NamedStoredProcedureQuery(name = "getStoreSales", procedureName = "get_store_sales", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_year", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_month", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_day", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_store_id", type = Long.class)
        })
})
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
