package com.choice.product.entity;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_img")
@Data
// 상품 이미지 엔티티
public class ProductImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pimgId; // 상품 이미지 ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 엔티티와의 일대다 관계

    private String pimgName; // 이미지 이름

    private String pimgPath; // 이미지 파일 경로

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ProductImg))
            return false;
        ProductImg that = (ProductImg) o;
        return Objects.equals(getPimgId(), that.getPimgId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPimgId());
    }

}