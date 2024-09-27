package com.choice.shopping.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.choice.shopping.entity.Orders.OrderStatus;
import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private String username;
    private List<OrderItemResponseDTO> orderItems;
    private Long totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private ShippingAddressDTO shippingAddress;
}