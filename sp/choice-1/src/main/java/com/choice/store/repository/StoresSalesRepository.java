package com.choice.store.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.store.entity.StoresSales;

public interface StoresSalesRepository extends JpaRepository<StoresSales, Long> {

        // 1. 상품별 판매량 조회 년도별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE YEAR(s.sale_date) = :year " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByYear(@Param("year") int year);

        // 2. 상품별 판매량 조회 년도별 월별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByYearAndMonth(@Param("year") int year, @Param("month") int month);

        // 3. 상품별 판매량 조회 날짜별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE s.sale_date = :date " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByDate(@Param("date") Date date);

        // 4. 상품별 판매량 조회 년도별 월별 일별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month AND DAY(s.sale_date) = :day " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByYearMonthDay(@Param("year") int year, @Param("month") int month,
                        @Param("day") int day);

        // 5. 상품별 판매량 조회 매장별 년도별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE s.store_id = :storeId AND YEAR(s.sale_date) = :year " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByStoreAndYear(@Param("storeId") Long storeId, @Param("year") int year);

        // 6. 상품별 판매량 조회 매장별 년도별 월별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE s.store_id = :storeId AND YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByStoreAndYearAndMonth(@Param("storeId") Long storeId,
                        @Param("year") int year, @Param("month") int month);

        // 7. 상품별 판매량 조회 매장별 날짜별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE s.store_id = :storeId AND s.sale_date = :date " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByStoreAndDate(@Param("storeId") Long storeId, @Param("date") Date date);

        // 8. 상품별 판매량 조회 매장별 년도별 월별 일별
        @Query(value = "SELECT p.product_id, p.product_name, SUM(s.quantity) as total_quantity " +
                        "FROM stores_sales s " +
                        "JOIN stores_products p ON s.product_id = p.product_id " +
                        "WHERE s.store_id = :storeId AND YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month AND DAY(s.sale_date) = :day "
                        +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT 5", nativeQuery = true)
        List<Object[]> findTop5SellingProductsByStoreAndYearMonthDay(@Param("storeId") Long storeId,
                        @Param("year") int year, @Param("month") int month, @Param("day") int day);

        // 1.매장별 매출 조회 년도별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE YEAR(s.sale_date) = :year " +
                        "GROUP BY s.store_id, st.store_name " +
                        "ORDER BY total_sales DESC", nativeQuery = true)
        List<Object[]> findSalesByStoreAndYear(@Param("year") int year);

        // 2.매장별 매출 조회 년도별 월별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month " +
                        "GROUP BY s.store_id, st.store_name " +
                        "ORDER BY total_sales DESC", nativeQuery = true)
        List<Object[]> findSalesByStoreAndYearAndMonth(@Param("year") int year, @Param("month") int month);

        // 3.매장별 매출 조회 날짜별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE s.sale_date = :date " +
                        "GROUP BY s.store_id, st.store_name " +
                        "ORDER BY total_sales DESC", nativeQuery = true)
        List<Object[]> findSalesByStoreAndDate(@Param("date") Date date);

        // 4.매장별 매출 조회 년도별 월별 일별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month AND DAY(s.sale_date) = :day " +
                        "GROUP BY s.store_id, st.store_name " +
                        "ORDER BY total_sales DESC", nativeQuery = true)
        List<Object[]> findSalesByStoreAndYearMonthDay(@Param("year") int year, @Param("month") int month,
                        @Param("day") int day);

        // 5.매장 매출 조회 년도별 월별 일별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE s.store_id = :storeId AND YEAR(s.sale_date) = :year " +
                        "GROUP BY s.store_id, st.store_name", nativeQuery = true)
        Object[] findSalesByStoreIdAndYear(@Param("storeId") Long storeId, @Param("year") int year);

        // 6. 매장 매출 조회 년도별 월별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE s.store_id = :storeId AND YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month " +
                        "GROUP BY s.store_id, st.store_name", nativeQuery = true)
        Object[] findSalesByStoreIdAndYearAndMonth(@Param("storeId") Long storeId, @Param("year") int year,
                        @Param("month") int month);

        // 7. 매장 매출 조회 날짜별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE s.store_id = :storeId AND s.sale_date = :date " +
                        "GROUP BY s.store_id, st.store_name", nativeQuery = true)
        Object[] findSalesByStoreIdAndDate(@Param("storeId") Long storeId, @Param("date") Date date);

        // 8. 매장 매출 조회 년도별 월별 일별
        @Query(value = "SELECT s.store_id, st.store_name, SUM(s.price * s.quantity) as total_sales " +
                        "FROM stores_sales s " +
                        "JOIN stores st ON s.store_id = st.store_id " +
                        "WHERE s.store_id = :storeId AND YEAR(s.sale_date) = :year AND MONTH(s.sale_date) = :month AND DAY(s.sale_date) = :day "
                        +
                        "GROUP BY s.store_id, st.store_name", nativeQuery = true)
        Object[] findSalesByStoreIdAndYearMonthDay(@Param("storeId") Long storeId, @Param("year") int year,
                        @Param("month") int month, @Param("day") int day);
}
