package com.choice.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.ai.service.FlaskClientService;
import com.choice.product.dto.ProductDetailDTO;
import com.choice.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminRecommendationController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FlaskClientService flaskClientService;

    @PostMapping("/recommend-products")
    public ResponseEntity<?> recommendProducts(
            @RequestParam(value = "productId") Long productId) {
        try {
            log.info("recommendProducts 호출");
            ProductDetailDTO productDetail = productService.getProductDetail(productId);
            log.info("productDetail: {}", productDetail.toString());

            // ProductDetailDTO를 Map<String, Object>로 변환
            Map<String, Object> productDetailMap = convertProductDetailToMap(productDetail);

            List<Map<String, Object>> productDetailList = new ArrayList<>();
            productDetailList.add(productDetailMap);
            log.info("productDetailList: {}", productDetailList.toString());

            List<Map<String, Object>> result = flaskClientService.getAdminRecommendations(productDetailList);
            log.info("recommendProducts 결과: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("추천 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("추천 상품 조회 중 오류 발생: " + e.getMessage());
        } finally {
            log.info("recommendProducts 종료");
        }
    }

    // ProductDetailDTO를 Map<String, Object>로 변환하는 메서드
    private Map<String, Object> convertProductDetailToMap(ProductDetailDTO productDetail) {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productDetail.getProductId());
        map.put("productName", productDetail.getProductName());
        map.put("info", productDetail.getInfo());
        map.put("price", productDetail.getPrice());
        map.put("likeCount", productDetail.getLikeCount());
        map.put("viewCount", productDetail.getViewCount());
        map.put("category", productDetail.getCategory());
        map.put("pimgName", productDetail.getPimgName());
        map.put("pimgPath", productDetail.getPimgPath());
        map.put("attributes", productDetail.getAttributes());
        map.put("similarProducts", productDetail.getSimilarProducts());
        return map;
    }
}