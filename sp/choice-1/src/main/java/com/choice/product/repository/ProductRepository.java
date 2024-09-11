package com.choice.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId IN :ids")
    List<Product> findAllWithImagesById(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.attributeLinks al " +
            "LEFT JOIN FETCH al.attribute " +
            "WHERE p.productId IN :productIds")
    List<Product> findAllWithImagesAndAttributesById(@Param("productIds") List<Long> productIds);
}
