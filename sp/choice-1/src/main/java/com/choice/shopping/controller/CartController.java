package com.choice.shopping.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.entity.Member;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.dto.CartTotalDTO;
import com.choice.shopping.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String cartItems) {
        try {
            List<CartItemDTO> items;
            if (userDetails != null) {
                Long userId = ((Member) userDetails).getUserId();
                items = cartService.getCartItems(userId);
            } else {
                items = cartService.getCartItems(cartItems);
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("장바구니 아이템을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeCart(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<CartItemDTO> localCartItems) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Long userId = ((Member) userDetails).getUserId();
            cartService.mergeCart(userId, localCartItems);
            return new ResponseEntity<>("장바구니가 성공적으로 병합되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("장바구니 병합 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니 총합 조회
    @GetMapping("/{userId}/total")

    public ResponseEntity<?> getCartTotal(@PathVariable Long userId) {
        try {
            Long total = cartService.getCartTotal(userId);
            return new ResponseEntity<>(new CartTotalDTO(total), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("장바구니 총합을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니 요약 조회
    @GetMapping("/{userId}/summary")
    public ResponseEntity<?> getCartSummary(@PathVariable Long userId) {
        try {
            CartSummaryDTO summary = cartService.getCartSummary(userId);
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("장바구니 요약을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
