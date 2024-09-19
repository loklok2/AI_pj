package com.choice.product.dto;

import lombok.Data;

@Data
public class ProductAllDTO {
    private Long productId;
    private String name;
    private String info;
    private Long price;
    private Long likeCount;
    private Long view;
    private String pimgName;
    private String pimgPath;
}
