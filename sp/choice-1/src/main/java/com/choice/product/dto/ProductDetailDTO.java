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
    private Long view;
    private String category;
    private List<String> images;
    private List<AttributeDTO> attributes;
}
