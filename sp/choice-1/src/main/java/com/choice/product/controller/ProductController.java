package com.choice.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.dto.ProductAllDTO;
import com.choice.product.dto.ProductDetailDTO;
import com.choice.product.dto.ProductRecommendationDTO;
import com.choice.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;

    // 상품 전체 조회
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "25") Integer size,
            @RequestParam(name = "sort", defaultValue = "productPriceHigh") String sort,
            @RequestParam(name = "category", required = false) String category) {

        Pageable pageable = null;

        if (sort.equalsIgnoreCase("productPriceHigh")) {
            pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "price"));
        } else if (sort.equalsIgnoreCase("productPriceLow")) {
            pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "price"));
        } else if (sort.equalsIgnoreCase("likeCount")) {
            pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        }

        Page<ProductAllDTO> products = productService.getAllProducts(category, pageable);
        return ResponseEntity.ok(products);
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable("id") Long id) {
        ProductDetailDTO product = productService.getProductDetail(id);
        List<ProductRecommendationDTO> similarProducts = productService.getSimilarProducts(id);
        product.setSimilarProducts(similarProducts);
        return ResponseEntity.ok(product);
    }

    // 상품 좋아요 처리
    @PostMapping("/{productId}/toggle-like")
    public ResponseEntity<?> toggleLike(@PathVariable("productId") Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            boolean isLiked = productService.toggleLike(member.getUserId(), productId);
            String message = isLiked ? "상품을 좋아요 했습니다." : "상품 좋아요를 취소했습니다.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("좋아요 처리 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
