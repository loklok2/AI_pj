package com.choice.admin.dto;

import java.time.LocalDateTime;

import com.choice.shopping.entity.Orders;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long orderId;
    private String username;
    private LocalDateTime orderDate;
    private Long totalAmount;
    private Orders.OrderStatus orderStatus;

}
