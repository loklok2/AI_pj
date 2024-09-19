package com.choice.store.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.store.entity.StoresSales;

public interface StoresSalesRepository extends JpaRepository<StoresSales, Long> {
    @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
            "FROM stores_sales s " +
            "JOIN stores_products p ON s.product_id = p.product_id " +
            "WHERE YEAR(s.sale_date) = :year " +
            "GROUP BY p.product_id, p.product_name " +
            "ORDER BY total_quantity DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5SellingProductsByYear(@Param("year") int year);

    @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
            "FROM stores_sales s " +
            "JOIN stores_products p ON s.product_id = p.product_id " +
            "WHERE YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month " +
            "GROUP BY p.product_id, p.product_name " +
            "ORDER BY total_quantity DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5SellingProductsByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
            "FROM stores_sales s " +
            "JOIN stores_products p ON s.product_id = p.product_id " +
            "WHERE s.sale_date = :date " +
            "GROUP BY p.product_id, p.product_name " +
            "ORDER BY total_quantity DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5SellingProductsByDate(@Param("date") Date date);

    @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
            "FROM stores_sales s " +
            "JOIN stores_products p ON s.product_id = p.product_id " +
            "WHERE YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month AND DAY(s.sale_date) = :day " +
            "GROUP BY p.product_id, p.product_name " +
            "ORDER BY total_quantity DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5SellingProductsByYearMonthDay(@Param("year") int year, @Param("month") int month,
            @Param("day") int day);
}
