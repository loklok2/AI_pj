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

    private List<Map<String, Object>> convertToMapList(List<Object[]> results) {
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", result[0]);
            map.put("productName", result[1]);
            map.put("totalQuantity", result[2]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTop5SellingProductsByYearMonthDay(int year, int month, int day) {
        List<Object[]> results = storesSalesRepository.findTop5SellingProductsByYearMonthDay(year, month, day);
        return convertToMapList(results);
    }
}
