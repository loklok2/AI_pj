package com.choice.shopping.dto;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long price;
    private String size;
    private String pimgPath;
}