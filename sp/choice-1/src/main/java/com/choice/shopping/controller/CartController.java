package com.choice.shopping.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.service.CartService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MemberRepository memberRepository;

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails != null) {
                String username = userDetails.getUsername();
                Member member = memberRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                CartSummaryDTO cartSummary = cartService.getCartItemsUser(member.getUserId());
                return new ResponseEntity<>(cartSummary, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("장바구니 아이템을 가져오는 중 오류가 발생했습니다", e);
            return new ResponseEntity<>("장바구니 아이템을 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 장바구니에 상품 추가
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody List<CartItemDTO> cartItemsDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("장바구니 아이템 추가 요청 받음");
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }
            log.info("로그인 성공");
            String username = userDetails.getUsername();
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            Long userId = member.getUserId();
            log.info("사용자 아이디 찾음");
            // 여러 CartItemDTO를 처리
            List<CartItemDTO> addedItems = new ArrayList<>();

            for (CartItemDTO cartItemDTO : cartItemsDTO) {
                log.info(cartItemDTO.toString());
                Long productId = cartItemDTO.getProductId();
                Integer quantity = cartItemDTO.getQuantity();
                String size = cartItemDTO.getSize();
                CartItemDTO addedItem = cartService.addToCart(userId, productId, quantity, size);
                addedItems.add(addedItem);
            }
            log.info("장바구니 아이템 추가 완료");

            return ResponseEntity.ok(addedItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상품 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeFromCart(@RequestParam("productId") Long productId,
            // 장바구니에 존재하는 상품일경우의 삭제하는 코드 추가 필요
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }
            String username = userDetails.getUsername();
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            Long userId = member.getUserId();
            cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok("상품이 장바구니에서 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(
            @RequestBody List<CartItemDTO> cartItemDTOs, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            String username = userDetails.getUsername();
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            Long userId = member.getUserId();

            List<CartItemDTO> updatedCartItems = new ArrayList<>();
            for (CartItemDTO cartItemDTO : cartItemDTOs) {
                CartItemDTO updatedCartItem = cartService.updateCartItem(userId, cartItemDTO);
                updatedCartItems.add(updatedCartItem);
            }

            return ResponseEntity.ok(updatedCartItems);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("장바구니 아이템 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    // // 장바구니 총합 조회
    // @GetMapping("/{userId}/total")

    // public ResponseEntity<?> getCartTotal(@PathVariable Long userId) {
    // try {
    // Long total = cartService.getCartTotal(userId);
    // return new ResponseEntity<>(new CartTotalDTO(total), HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>("장바구니 총합을 가져오는 중 오류가 발생했습니다.",
    // HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }

}
