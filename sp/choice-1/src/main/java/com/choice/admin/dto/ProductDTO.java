package com.choice.admin.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String info;
    private Long sell;
    private Long price;
    private Long likeCount;
    private Long view;
    private Set<Long> attributeIds;
}