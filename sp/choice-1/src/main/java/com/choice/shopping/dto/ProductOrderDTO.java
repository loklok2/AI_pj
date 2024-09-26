package com.choice.shopping.dto;

import lombok.Data;

@Data
public class ProductOrderDTO {
    private Long productId;
    private Integer quantity;
    private String recipientName;
    private String address;
    private String phone;
    private String deliveryInstructions;
}
