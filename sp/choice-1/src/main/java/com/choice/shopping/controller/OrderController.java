package com.choice.shopping.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.dto.OrderDTO;
import com.choice.shopping.dto.ProductOrderDTO;
import com.choice.shopping.entity.Orders;
import com.choice.shopping.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    // 주문 내역 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable("userId") Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (!member.getUserId().equals(userId)) {
                return new ResponseEntity<>("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("주문 내역을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니에서 주문하는것으로 가는거
    @PostMapping("/create-from-cart")
    public ResponseEntity<?> createOrderFromCart(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductOrderDTO productOrderDTO) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Orders order = orderService.createOrderFromCart(member.getUserId(), productOrderDTO);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("주문 생성 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-from-product")
    public ResponseEntity<?> createOrderFromProduct(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductOrderDTO productOrderDTO) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Orders order = orderService.createOrderFromProduct(member.getUserId(), productOrderDTO);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("주문 생성 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
