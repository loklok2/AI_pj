package com.choice.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long userId;
    private String sessionId;
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String category;
    private Integer quantity;
    private String size;
    private Long price;
    private Long totalPrice;
    private String pimgPath; // pimgPath 추가
}