package com.choice.product.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductDetailDTO {
    private Long productId;
    private String name;
    private String info;
    private Long price;
    private Long likeCount;
    private Long viewCount;
    private String category;
    private String pimgName;
    private String pimgPath;
    private List<AttributeDTO> attributes;
    private List<ProductRecommendationDTO> randomRecommendations;
}
