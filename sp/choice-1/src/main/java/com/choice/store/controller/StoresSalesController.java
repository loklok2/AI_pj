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

    // 상품 판매 상위 5개 조회
    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(
            @RequestParam("year") Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "day", required = false) Integer day,
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return ResponseEntity.ok(storesSalesService.getTopSellingProducts(year, month, day, storeId));
    }

    // 매장 판매금액 조회
    @GetMapping("/store-sales")
    public ResponseEntity<Map<String, Object>> getStoreSales(
            @RequestParam("fromYear") Integer fromYear,
            @RequestParam(value = "fromMonth", required = false) Integer fromMonth,
            @RequestParam(value = "fromDay", required = false) Integer fromDay,
            @RequestParam(value = "toYear", required = false) Integer toYear,
            @RequestParam(value = "toMonth", required = false) Integer toMonth,
            @RequestParam(value = "toDay", required = false) Integer toDay,
            @RequestParam(value = "storeId", required = false) Long storeId) {

        if (toYear == null)
            toYear = fromYear;
        if (toMonth == null)
            toMonth = fromMonth != null ? fromMonth : 12;
        if (toDay == null)
            toDay = fromDay != null ? fromDay : 31;

        Map<String, Object> result = storesSalesService.getStoreSales(fromYear, fromMonth, fromDay, toYear, toMonth,
                toDay, storeId);

        String searchDateRange = formatSearchDateRange(fromYear, fromMonth, fromDay, toYear, toMonth, toDay);
        result.put("searchDateRange", searchDateRange);

        return ResponseEntity.ok(result);
    }

    private String formatSearchDateRange(Integer fromYear, Integer fromMonth, Integer fromDay,
            Integer toYear, Integer toMonth, Integer toDay) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatDate(fromYear, fromMonth, fromDay));
        sb.append(" ~ ");
        sb.append(formatDate(toYear, toMonth, toDay));
        return sb.toString();
    }

    private String formatDate(Integer year, Integer month, Integer day) {
        StringBuilder sb = new StringBuilder().append(year);
        if (month != null) {
            sb.append(".").append(String.format("%02d", month));
            if (day != null) {
                sb.append(".").append(String.format("%02d", day));
            }
        }
        return sb.toString();
    }

    // private int getLastDayOfMonth(int year, int month) {
    // return YearMonth.of(year, month).lengthOfMonth();
    // }

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