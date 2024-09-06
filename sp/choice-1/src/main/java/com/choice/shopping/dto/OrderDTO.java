package com.choice.shopping.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.choice.shopping.entity.Orders.OrderStatus;

import lombok.Data;

@Data
public class OrderDTO {
    private Long orderId;
    private List<OrderItemDTO> orderItems;
    private Long totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
}
