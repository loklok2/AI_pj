package com.choice.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.product.entity.Product;
import com.choice.product.entity.ProductInventory;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByProduct(Product product);
}
