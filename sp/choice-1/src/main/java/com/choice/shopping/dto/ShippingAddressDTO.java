package com.choice.shopping.dto;

import lombok.Data;

@Data
public class ShippingAddressDTO {
    private Long addressId;
    private String recipientName;
    private String address;
    private String phone;
    private String deliveryInstructions;
}