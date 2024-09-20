package com.choice.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long productId;
    private String pimgPath;
    private String productName;
    private String category;
    private Integer quantity;
    private Long price;
    private Long totalPrice;

}