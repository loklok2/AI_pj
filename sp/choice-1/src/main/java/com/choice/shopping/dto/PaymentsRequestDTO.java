package com.choice.shopping.dto;

import java.util.List;

import lombok.Data;

@Data
public class PaymentsRequestDTO {
    private String merchantUid;// 주문번호
    private Long userId;// 회원번호
    private List<OrderItemDTO> orderItems;
    private String paymentMethod; // 결제방법 ex) 네이버페이, 카카오페이, 카드
    private String paymentStatus; // 결제상태 ex) 결제대기, 결제완료, 결제실패
    private String payValue; // 결제금액
}
