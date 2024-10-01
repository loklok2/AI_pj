package com.choice.product.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.product.entity.SimilarProduct;

public interface SimilarProductRepository extends JpaRepository<SimilarProduct, Long> {
    List<SimilarProduct> findByProduct_ProductId(Long productId);
}