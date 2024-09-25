package com.choice.product.dto;

import lombok.Data;

@Data
public class ProductRecommendationDTO {
    private Long productId;
    private String name;
    private Long price;
    private String pimgName;
    private String pimgPath;
}