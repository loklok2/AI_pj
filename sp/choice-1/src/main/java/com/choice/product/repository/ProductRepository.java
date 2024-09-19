package com.choice.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId IN :ids")
    List<Product> findAllWithImagesById(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM product_details_view WHERE product_id IN (:productIds)", nativeQuery = true)
    List<Object[]> findProductDetailsById(@Param("productIds") List<Long> productIds);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.images", countQuery = "SELECT COUNT(p) FROM Product p")
    Page<Product> findAllWithImages(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.attributeLinks WHERE p.productId = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);
}
