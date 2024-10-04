package com.choice.store.service;

// import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;
import com.choice.store.repository.StoresSalesRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class StoresSalesService {
    @Autowired
    private StoresSalesRepository storesSalesRepository;

    @Autowired
    private ProductRepository productRepository;

    // 상품 판매 상위 5개 조회
    public List<Map<String, Object>> getTopSellingProducts(Integer year, Integer month, Integer day, Long storeId) {
        List<Object[]> results = storesSalesRepository.getTopSellingProducts(year, month, day, storeId);
        return convertToProductMapList(results);
    }

    // 매장 매출 조회
    public Map<String, Object> getStoreSales(Integer fromYear, Integer fromMonth, Integer fromDay,
            Integer toYear, Integer toMonth, Integer toDay, Long storeId) {
        List<Object[]> results = storesSalesRepository.getStoreSales(fromYear, fromMonth, fromDay, toYear, toMonth,
                toDay, storeId);

        double totalSales = 0.0;
        List<Map<String, Object>> salesData = convertToSalesMapList(results, fromMonth == null, fromDay == null);
        for (Map<String, Object> sale : salesData) {
            totalSales += ((Number) sale.get("totalSales")).doubleValue();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("salesData", salesData);
        response.put("totalSales", String.format("%,d", (long) totalSales));
        return response;
    }

    private List<Map<String, Object>> convertToProductMapList(List<Object[]> results) {
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            Long productId = ((Number) result[0]).longValue();
            map.put("productId", productId);
            map.put("productName", result[1]);
            map.put("totalQuantity", result[2]);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            String imagePath = null;
            if (!product.getImages().isEmpty()) {
                ProductImg firstImage = product.getImages().iterator().next();
                imagePath = "/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName();
            }
            map.put("pimgPath", imagePath);

            log.info("map: {}", map.toString());
            return map;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> convertToSalesMapList(List<Object[]> results, boolean isYearlyQuery,
            boolean isMonthlyQuery) {
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("storeId", result[0]);
            map.put("storeName", result[1]);
            if (isYearlyQuery || isMonthlyQuery) {
                map.put("year", result[2]);
                map.put("month", result[3]);
                map.put("totalSales", result[4]);
            } else {
                map.put("date", result[2]);
                map.put("totalSales", result[3]);
            }
            return map;
        }).collect(Collectors.toList());
    }

    // public List<Map<String, Object>> getTop5SellingProductsByYear(int year) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByYear(year);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>> getTop5SellingProductsByYearAndMonth(int
    // year, int month) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByYearAndMonth(year, month);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>> getTop5SellingProductsByDate(Date date) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByDate(date);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>> getTop5SellingProductsByYearMonthDay(int
    // year, int month, int day) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByYearMonthDay(year, month,
    // day);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>> getTop5SellingProductsByStoreAndYear(Long
    // storeId, int year) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByStoreAndYear(storeId, year);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>>
    // getTop5SellingProductsByStoreAndYearAndMonth(Long storeId, int year, int
    // month) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByStoreAndYearAndMonth(storeId,
    // year,
    // month);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>> getTop5SellingProductsByStoreAndDate(Long
    // storeId, Date date) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByStoreAndDate(storeId, date);
    // return convertToMapList(results);
    // }

    // public List<Map<String, Object>>
    // getTop5SellingProductsByStoreAndYearMonthDay(Long storeId, int year, int
    // month,
    // int day) {
    // List<Object[]> results =
    // storesSalesRepository.findTop5SellingProductsByStoreAndYearMonthDay(storeId,
    // year,
    // month, day);
    // return convertToMapList(results);
    // }

    // private List<Map<String, Object>> convertToMapList(List<Object[]> results) {
    // return results.stream().map(result -> {
    // Map<String, Object> map = new HashMap<>();
    // map.put("productId", result[0]);
    // map.put("productName", result[1]);
    // map.put("totalQuantity", result[2]);
    // return map;
    // }).collect(Collectors.toList());
    // }

    // public List<Map<String, Object>> getSalesByStoreAndYear(int year) {
    // List<Object[]> results = storesSalesRepository.findSalesByStoreAndYear(year);
    // return convertToSalesMapList(results);
    // }

    // public List<Map<String, Object>> getSalesByStoreAndYearAndMonth(int year, int
    // month) {
    // List<Object[]> results =
    // storesSalesRepository.findSalesByStoreAndYearAndMonth(year, month);
    // return convertToSalesMapList(results);
    // }

    // public List<Map<String, Object>> getSalesByStoreAndDate(Date date) {
    // List<Object[]> results = storesSalesRepository.findSalesByStoreAndDate(date);
    // return convertToSalesMapList(results);
    // }

    // public List<Map<String, Object>> getSalesByStoreAndYearMonthDay(int year, int
    // month, int day) {
    // List<Object[]> results =
    // storesSalesRepository.findSalesByStoreAndYearMonthDay(year, month, day);
    // return convertToSalesMapList(results);
    // }

    // private List<Map<String, Object>> convertToSalesMapList(List<Object[]>
    // results) {
    // return results.stream().map(result -> {
    // Map<String, Object> map = new HashMap<>();
    // map.put("storeId", result[0]);
    // map.put("storeName", result[1]);
    // map.put("totalSales", result[2]);
    // return map;
    // }).collect(Collectors.toList());
    // }

    // public Map<String, Object> getSalesByStoreIdAndYear(Long storeId, int year) {
    // Object[] result = storesSalesRepository.findSalesByStoreIdAndYear(storeId,
    // year);
    // if (result == null) {
    // return null;
    // }
    // Map<String, Object> map = new HashMap<>();
    // map.put("storeId", result[0]);
    // map.put("storeName", result[1]);
    // map.put("totalSales", result[2]);
    // return map;
    // }

    // public Map<String, Object> getSalesByStoreIdAndYearAndMonth(Long storeId, int
    // year, int month) {
    // Object[] result =
    // storesSalesRepository.findSalesByStoreIdAndYearAndMonth(storeId, year,
    // month);
    // return convertToSalesMap(result);
    // }

    // public Map<String, Object> getSalesByStoreIdAndDate(Long storeId, Date date)
    // {
    // Object[] result = storesSalesRepository.findSalesByStoreIdAndDate(storeId,
    // date);
    // return convertToSalesMap(result);
    // }

    // public Map<String, Object> getSalesByStoreIdAndYearMonthDay(Long storeId, int
    // year, int month, int day) {
    // Object[] result =
    // storesSalesRepository.findSalesByStoreIdAndYearMonthDay(storeId, year, month,
    // day);
    // return convertToSalesMap(result);
    // }

    // private Map<String, Object> convertToSalesMap(Object[] result) {
    // if (result == null) {
    // return null;
    // }
    // Map<String, Object> map = new HashMap<>();
    // map.put("storeId", result[0]);
    // map.put("storeName", result[1]);
    // map.put("totalSales", result[2]);
    // return map;
    // }
}
