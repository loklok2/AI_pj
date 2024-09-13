package com.choice.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId IN :ids")
    List<Product> findAllWithImagesById(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM product_details_view WHERE product_id IN (:productIds)", nativeQuery = true)
    List<Object[]> findProductDetailsById(@Param("productIds") List<Long> productIds);
}
