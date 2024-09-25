package com.choice.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.service.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        try {
            CartSummaryDTO summary;
            if (userDetails != null) {
                String username = userDetails.getUsername();
                log.debug("Fetching cart items for authenticated user: {}", username);
                Member member = memberRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                summary = cartService.getCartItemsUser(member.getUserId());
            } else {
                String sessionId = session.getId();
                log.debug("Fetching cart items for session: {}", sessionId);
                summary = cartService.getCartItemsSession(sessionId);
            }
            log.debug("Retrieved {} cart items", summary.getItems().size());
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching cart items", e);
            return new ResponseEntity<>("장바구니 아이템을 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO cartItemDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }
            String username = userDetails.getUsername();
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            Long userId = member.getUserId();
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();
            CartItemDTO addedItem = cartService.addToCart(userId, productId, quantity);
            return ResponseEntity.ok(addedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상품 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Long userId = ((Member) userDetails).getUserId();
            String sessionId = session.getId();
            cartService.mergeCart(userId, sessionId);
            return new ResponseEntity<>("장바구니가 성공적으로 병합되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("장바구니 병합 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeFromCart(@RequestParam("productId") Long productId,
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
