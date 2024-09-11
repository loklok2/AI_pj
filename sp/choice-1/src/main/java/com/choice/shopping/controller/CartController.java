package com.choice.shopping.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.dto.CartTotalDTO;
import com.choice.shopping.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {
        List<CartItemDTO> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/{userId}/total")
    public ResponseEntity<CartTotalDTO> getCartTotal(@PathVariable Long userId) {
        Long total = cartService.getCartTotal(userId);
        return ResponseEntity.ok(new CartTotalDTO(total));
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<CartSummaryDTO> getCartSummary(@PathVariable Long userId) {
        CartSummaryDTO summary = cartService.getCartSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
