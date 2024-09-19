package com.choice.store.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choice.store.service.StoresSalesService;

@RestController
@RequestMapping("/api/sales")
public class StoresSalesController {
    @Autowired
    private StoresSalesService storesSalesService;

    @GetMapping("/top5/{year}")
    public ResponseEntity<List<Map<String, Object>>> getTop5SellingProductsByYear(@PathVariable("year") int year) {
        return ResponseEntity.ok(storesSalesService.getTop5SellingProductsByYear(year));
    }

    @GetMapping("/top5/{year}/{month}")
    public ResponseEntity<List<Map<String, Object>>> getTop5SellingProductsByYearAndMonth(
            @PathVariable("year") int year, @PathVariable("month") int month) {
        return ResponseEntity.ok(storesSalesService.getTop5SellingProductsByYearAndMonth(year, month));
    }

    @GetMapping("/top5/date/{date}")
    public ResponseEntity<List<Map<String, Object>>> getTop5SellingProductsByDate(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ResponseEntity.ok(storesSalesService.getTop5SellingProductsByDate(date));
    }

    @GetMapping("/top5/{year}/{month}/{day}")
    public ResponseEntity<List<Map<String, Object>>> getTop5SellingProductsByYearMonthDay(
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @PathVariable("day") int day) {
        return ResponseEntity.ok(storesSalesService.getTop5SellingProductsByYearMonthDay(year, month, day));
    }
}