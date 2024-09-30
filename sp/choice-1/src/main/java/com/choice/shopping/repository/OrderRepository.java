package com.choice.shopping.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.shopping.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
        // 회원의 주문 내역 조회
        List<Orders> findByMember_UserId(Long userId);

        // 일별 매출 조회
        @Query(value = "SELECT * FROM daily_sales_report WHERE date BETWEEN :startDate AND :endDate", nativeQuery = true)
        List<Object[]> findDailySalesReport(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // 월별 매출 조회
        @Query(value = "SELECT * FROM monthly_sales_report WHERE month BETWEEN :startDate AND :endDate", nativeQuery = true)
        List<Object[]> findMonthlySalesReport(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // 카테고리별 매출 조회
        @Query(nativeQuery = true, value = "SELECT * FROM category_sales_percentage")
        List<Object[]> findCategorySalesPercentage();

        @Query("SELECT DISTINCT o FROM Orders o " +
                        "LEFT JOIN FETCH o.member " +
                        "LEFT JOIN FETCH o.orderItems oi " +
                        "LEFT JOIN FETCH oi.product p " +
                        "LEFT JOIN FETCH p.images " +
                        "ORDER BY o.orderDate DESC")
        List<Orders> findAllWithDetails();

        Optional<Orders> findByMerchantUid(String merchantUid);

        List<Orders> findByMemberUserIdOrderByOrderDateDesc(Long userId);

}