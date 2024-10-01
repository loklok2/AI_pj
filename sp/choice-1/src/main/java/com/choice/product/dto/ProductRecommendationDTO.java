package com.choice.product.dto;

import lombok.Data;

@Data
public class ProductRecommendationDTO {
    private Long productId;
    private String name;
    private Long price;
    private String pimgName;
    private String pimgPath;

    public ProductRecommendationDTO(Long productId, String name, Long price, String pimgPath) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.pimgPath = pimgPath;
    }
}