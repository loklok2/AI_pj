package com.choice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}