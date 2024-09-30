package com.choice.shopping.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderListResponseDTO {
    private Long orderId;
    private String merchantUid;
    private LocalDateTime orderDate;
    private String orderStatus;
    private List<OrderItemDTO> orderItems;
}
