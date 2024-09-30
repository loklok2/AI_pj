package com.choice.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private Long orderId;
    private String recipientName;
    private String orderDate;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientMessage;
}
