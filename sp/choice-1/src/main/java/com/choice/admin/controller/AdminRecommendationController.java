package com.choice.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.admin.service.AdminRecommendationService;

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
            return ResponseEntity.ok(adminRecommendationService.getRecommendedProducts(year, month, day, storeId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("추천 상품 조회 중 오류 발생: " + e.getMessage());
        }
    }
}