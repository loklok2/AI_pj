package com.choice.shopping.dto;

import java.util.List;

import com.choice.shopping.entity.Orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 주문 응답 DTO
public class OrderResponseDTO {
    private Long orderId;
    private String merchantUid;
    private String orderDate;
    private String orderStatus;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientMessage;
    private String postCode;
    private List<OrderItemDTO> orderItems;

    // 주문 정보를 받아와서 주문 응답 DTO로 변환하는 생성자
    public OrderResponseDTO(Orders orders, List<OrderItemDTO> orderItems) {
        this.orderId = orders.getOrderId();
        this.merchantUid = orders.getMerchantUid();
        this.orderDate = orders.getOrderDate().toString();
        this.orderStatus = orders.getOrderStatus().toString();
        this.postCode = orders.getShippingAddress().getPostCode();
        this.orderItems = orderItems;
    }

}
