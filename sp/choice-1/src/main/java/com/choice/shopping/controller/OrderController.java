package com.choice.shopping.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.dto.OrderDetailDTO;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.OrderListResponseDTO;
import com.choice.shopping.dto.OrderRequsetDTO;
import com.choice.shopping.dto.OrderResponseDTO;
import com.choice.shopping.dto.PaymentsRequestDTO;
import com.choice.shopping.service.CartService;
import com.choice.shopping.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberRepository memberRepository;
    private final CartService cartService;

    // @Value("${imp.key}")
    // private String impKey;

    // @Value("${imp.secret}")
    // private String impSecret;
    // 나의 주문 목록조회
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderListResponseDTO>> getOrderList(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Member member = memberRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<OrderListResponseDTO> orderList = orderService.getOrderListForMember(member.getUserId());
        return ResponseEntity.ok(orderList);
    }

    // 나의 주문 상세 조회
    @GetMapping("/my-orders/{orderId}")
    public ResponseEntity<OrderDetailDTO> getOrderDetail(@PathVariable("orderId") Long orderId) {
        OrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(orderDetail);
    }

    // 주문 생성
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderRequsetDTO payload) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        List<OrderItemDTO> carts = payload.getOrderItems();
        OrderResponseDTO temporaryOrder = orderService.createOrder(payload, member, carts);
        return ResponseEntity.ok(temporaryOrder);
    }

    @PostMapping("/done/{imp_uid}")
    public ResponseEntity<Object> completeOrder(@PathVariable("imp_uid") String imp_uid,
            @RequestBody PaymentsRequestDTO payload) {
        orderService.completeOrder(payload);
        cartService.removeFromCart(payload.getUserId(), payload.getOrderItems().get(0).getProductId());
        return ResponseEntity.ok("결제 완료");
    }

}
