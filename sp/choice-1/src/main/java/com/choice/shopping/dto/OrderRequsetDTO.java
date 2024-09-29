package com.choice.shopping.dto;

import lombok.Data;
import java.util.List;

@Data
// 주문 요청 DTO
public class OrderRequsetDTO {
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientMessage;
    private List<OrderItemDTO> orderItems;
}
