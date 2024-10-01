package com.choice.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.admin.service.AdminRecommendationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminRecommendationController {

    @Autowired
    private AdminRecommendationService adminRecommendationService;

    @PostMapping("/recommend-products")
    public ResponseEntity<?> recommendProducts(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day,
            @RequestParam(value = "storeId", required = false) Long storeId) {
        try {
            log.info("recommendProducts 호출");
            List<Map<String, Object>> result = adminRecommendationService.getRecommendedProducts(year, month, day,
                    storeId);
            log.info("recommendProducts 결과: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("추천 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("추천 상품 조회 중 오류 발생: " + e.getMessage());
        } finally {
            log.info("recommendProducts 종료");
        }
    }
}