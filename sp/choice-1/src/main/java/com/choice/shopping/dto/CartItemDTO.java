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
    private Long userId; // 0
    private Long cartItemId; // 1
    private Long productId; // 2
    private String productName; // 3
    private String category; // 4
    private Integer quantity; // 5
    private Long price; // 6
    private Long totalPrice; // 7
    private String pimgPath; // 8
    private String size; // 9
}