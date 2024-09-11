package com.choice.shopping.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.shopping.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    // 회원의 주문 내역 조회
    List<Orders> findByMember_UserId(Long userId);

    // 일별 매출 조회
    @Query(value = "SELECT DATE(o.order_date) as date, SUM(oi.price * oi.quantity) as total_sales, COUNT(DISTINCT o.order_id) as order_count "
            +
            "FROM orders o " +
            "JOIN order_item oi ON o.order_id = oi.order_id " +
            "WHERE o.order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.order_date)", nativeQuery = true)
    List<Object[]> findDailySalesReport(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 월별 매출 조회
    @Query(value = "SELECT DATE_FORMAT(o.order_date, '%Y-%m') as month, SUM(oi.price * oi.quantity) as total_sales, COUNT(DISTINCT o.order_id) as order_count "
            +
            "FROM orders o " +
            "JOIN order_item oi ON o.order_id = oi.order_id " +
            "WHERE o.order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_FORMAT(o.order_date, '%Y-%m')", nativeQuery = true)
    List<Object[]> findMonthlySalesReport(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}