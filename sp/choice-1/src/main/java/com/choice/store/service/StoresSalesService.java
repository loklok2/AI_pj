package com.choice.store.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choice.store.repository.StoresSalesRepository;

@Service
public class StoresSalesService {
    @Autowired
    private StoresSalesRepository storesSalesRepository;

    public List<Map<String, Object>> getTop5SellingProductsByYear(int year) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByYear(year);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByYearAndMonth(int year, int month) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByYearAndMonth(year, month);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByDate(Date date) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByDate(date);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByYearMonthDay(int year, int month, int day) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByYearMonthDay(year, month, day);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByStoreAndYear(Long storeId, int year) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByStoreAndYear(storeId, year);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByStoreAndYearAndMonth(Long storeId, int year, int month) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByStoreAndYearAndMonth(storeId, year,
                month);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByStoreAndDate(Long storeId, Date date) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByStoreAndDate(storeId, date);
        return convertToMapList(results);
    }

    public List<Map<String, Object>> getTop5SellingProductsByStoreAndYearMonthDay(Long storeId, int year, int month,
            int day) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByStoreAndYearMonthDay(storeId, year,
                month, day);
        return convertToMapList(results);
    }

    private List<Map<String, Object>> convertToMapList(List<Object[]> results) {
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", result[0]);
            map.put("productName", result[1]);
            map.put("totalQuantity", result[2]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSalesByStoreAndYear(int year) {
        List<Object[]> results = storesSalesRepository.findSalesByStoreAndYear(year);
        return convertToSalesMapList(results);
    }

    public List<Map<String, Object>> getSalesByStoreAndYearAndMonth(int year, int month) {
        List<Object[]> results = storesSalesRepository.findSalesByStoreAndYearAndMonth(year, month);
        return convertToSalesMapList(results);
    }

    public List<Map<String, Object>> getSalesByStoreAndDate(Date date) {
        List<Object[]> results = storesSalesRepository.findSalesByStoreAndDate(date);
        return convertToSalesMapList(results);
    }

    public List<Map<String, Object>> getSalesByStoreAndYearMonthDay(int year, int month, int day) {
        List<Object[]> results = storesSalesRepository.findSalesByStoreAndYearMonthDay(year, month, day);
        return convertToSalesMapList(results);
    }

    private List<Map<String, Object>> convertToSalesMapList(List<Object[]> results) {
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("storeId", result[0]);
            map.put("storeName", result[1]);
            map.put("totalSales", result[2]);
            return map;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getSalesByStoreIdAndYear(Long storeId, int year) {
        Object[] result = storesSalesRepository.findSalesByStoreIdAndYear(storeId, year);
        if (result == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("storeId", result[0]);
        map.put("storeName", result[1]);
        map.put("totalSales", result[2]);
        return map;
    }

    public Map<String, Object> getSalesByStoreIdAndYearAndMonth(Long storeId, int year, int month) {
        Object[] result = storesSalesRepository.findSalesByStoreIdAndYearAndMonth(storeId, year, month);
        return convertToSalesMap(result);
    }

    public Map<String, Object> getSalesByStoreIdAndDate(Long storeId, Date date) {
        Object[] result = storesSalesRepository.findSalesByStoreIdAndDate(storeId, date);
        return convertToSalesMap(result);
    }

    public Map<String, Object> getSalesByStoreIdAndYearMonthDay(Long storeId, int year, int month, int day) {
        Object[] result = storesSalesRepository.findSalesByStoreIdAndYearMonthDay(storeId, year, month, day);
        return convertToSalesMap(result);
    }

    private Map<String, Object> convertToSalesMap(Object[] result) {
        if (result == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("storeId", result[0]);
        map.put("storeName", result[1]);
        map.put("totalSales", result[2]);
        return map;
    }
}
