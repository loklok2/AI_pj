package com.choice.store.controller;

// import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.store.service.StoresSalesService;

@RestController
@RequestMapping("/api/sales")
public class StoresSalesController {
    @Autowired
    private StoresSalesService storesSalesService;

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(
            @RequestParam("year") Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day,
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return ResponseEntity.ok(storesSalesService.getTopSellingProducts(year, month, day, storeId));
    }

    @GetMapping("/store-sales")
    public ResponseEntity<List<Map<String, Object>>> getStoreSales(
            @RequestParam("year") Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day,
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return ResponseEntity.ok(storesSalesService.getStoreSales(year, month, day, storeId));
    }

    // // 상품 매출 조회 년도별(전체 매장 리스트로 반환)
    // @GetMapping("/top5/{year}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByYear(@PathVariable("year") int year) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByYear(year));
    // }

    // // 상품 매출 조회 년도 + 월별(전체 매장 리스트로 반환)
    // @GetMapping("/top5/{year}/{month}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByYearAndMonth(
    // @PathVariable("year") int year, @PathVariable("month") int month) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByYearAndMonth(year,
    // month));
    // }

    // // 상품 매출 조회 날짜별(전체 매장 리스트로 반환)
    // @GetMapping("/top5/date/{date}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByDate(
    // @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByDate(date));
    // }

    // // 상품 매출 조회 년도 + 월 + 일별(전체 매장 리스트로 반환)
    // @GetMapping("/top5/{year}/{month}/{day}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByYearMonthDay(
    // @PathVariable("year") int year,
    // @PathVariable("month") int month,
    // @PathVariable("day") int day) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByYearMonthDay(year,
    // month, day));
    // }

    // // 매장 매출 조회 년도별(개별 매장 리스트로 반환)
    // @GetMapping("/top5/store/{storeId}/{year}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByStoreAndYear(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("year") int year) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByStoreAndYear(storeId,
    // year));
    // }

    // // 매장 매출 조회 년도 + 월별(개별 매장 리스트로 반환)
    // @GetMapping("/top5/store/{storeId}/{year}/{month}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByStoreAndYearAndMonth(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("year") int year,
    // @PathVariable("month") int month) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByStoreAndYearAndMonth(storeId,
    // year, month));
    // }

    // // 매장 매출 조회 날짜별(개별 매장 리스트로 반환)
    // @GetMapping("/top5/store/{storeId}/date/{date}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByStoreAndDate(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
    // return
    // ResponseEntity.ok(storesSalesService.getTop5SellingProductsByStoreAndDate(storeId,
    // date));
    // }

    // // 매장 매출 조회 년도 + 월 + 일별(개별 매장 리스트로 반환)
    // @GetMapping("/top5/store/{storeId}/{year}/{month}/{day}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getTop5SellingProductsByStoreAndYearMonthDay(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("year") int year,
    // @PathVariable("month") int month,
    // @PathVariable("day") int day) {
    // return ResponseEntity
    // .ok(storesSalesService.getTop5SellingProductsByStoreAndYearMonthDay(storeId,
    // year, month, day));
    // }

    // // 매장 매출 조회 년도별(전체 매장 리스트로 반환)
    // @GetMapping("/stores/{year}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getSalesByStoreAndYear(@PathVariable("year") int year) {
    // return ResponseEntity.ok(storesSalesService.getSalesByStoreAndYear(year));
    // }

    // // 매장 매출 조회 년도 + 월별(전체 매장 리스트로 반환)
    // @GetMapping("/stores/{year}/{month}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getSalesByStoreAndYearAndMonth(
    // @PathVariable("year") int year, @PathVariable("month") int month) {
    // return
    // ResponseEntity.ok(storesSalesService.getSalesByStoreAndYearAndMonth(year,
    // month));
    // }

    // // 매장 매출 조회 날짜별(전체 매장 리스트로 반환)
    // @GetMapping("/stores/date/{date}")
    // public ResponseEntity<List<Map<String, Object>>> getSalesByStoreAndDate(
    // @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
    // return ResponseEntity.ok(storesSalesService.getSalesByStoreAndDate(date));
    // }

    // // 매장 매출 조회 년도 + 월 + 일별(전체 매장 리스트로 반환)
    // @GetMapping("/stores/{year}/{month}/{day}")
    // public ResponseEntity<List<Map<String, Object>>>
    // getSalesByStoreAndYearMonthDay(
    // @PathVariable("year") int year,
    // @PathVariable("month") int month,
    // @PathVariable("day") int day) {
    // return
    // ResponseEntity.ok(storesSalesService.getSalesByStoreAndYearMonthDay(year,
    // month, day));
    // }

    // // 매장 매출 조회 년도별(개별 매장 리스트로 반환)
    // @GetMapping("/store/{storeId}/{year}")
    // public ResponseEntity<Map<String, Object>> getSalesByStoreIdAndYear(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("year") int year) {
    // Map<String, Object> result =
    // storesSalesService.getSalesByStoreIdAndYear(storeId, year);
    // if (result == null) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(result);
    // }

    // // 매장 매출 조회 년도 + 월별(개별 매장 리스트로 반환)
    // @GetMapping("/store/{storeId}/{year}/{month}")
    // public ResponseEntity<Map<String, Object>> getSalesByStoreIdAndYearAndMonth(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("year") int year,
    // @PathVariable("month") int month) {
    // Map<String, Object> result =
    // storesSalesService.getSalesByStoreIdAndYearAndMonth(storeId, year, month);
    // if (result == null) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(result);
    // }

    // // 매장 매출 조회 날짜별(개별 매장 리스트로 반환)
    // @GetMapping("/store/{storeId}/date/{date}")
    // public ResponseEntity<Map<String, Object>> getSalesByStoreIdAndDate(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
    // Map<String, Object> result =
    // storesSalesService.getSalesByStoreIdAndDate(storeId, date);
    // if (result == null) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(result);
    // }

    // // 매장 매출 조회 년도 + 월 + 일별(개별 매장 리스트로 반환)
    // @GetMapping("/store/{storeId}/{year}/{month}/{day}")
    // public ResponseEntity<Map<String, Object>> getSalesByStoreIdAndYearMonthDay(
    // @PathVariable("storeId") Long storeId,
    // @PathVariable("year") int year,
    // @PathVariable("month") int month,
    // @PathVariable("day") int day) {
    // Map<String, Object> result =
    // storesSalesService.getSalesByStoreIdAndYearMonthDay(storeId, year, month,
    // day);
    // if (result == null) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(result);
    // }

}