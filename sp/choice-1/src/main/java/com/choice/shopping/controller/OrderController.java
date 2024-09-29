package com.choice.shopping.controller;

import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.OrderRequsetDTO;
import com.choice.shopping.dto.OrderResponseDTO;
import com.choice.shopping.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberRepository memberRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderRequsetDTO payload) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        List<OrderItemDTO> carts = payload.getOrderItems();
        OrderResponseDTO temporaryOrder = orderService.createOrder(payload, member, carts);

        return ResponseEntity.ok(temporaryOrder);
    }

    @PostMapping("/done")
    public ResponseEntity<Object> completeOrder(@RequestBody OrderRequsetDTO request) {
        OrderResponseDTO orderResponseDTO = orderService.completeOrder(request);
        return ResponseEntity.ok(orderResponseDTO);
    }

}
