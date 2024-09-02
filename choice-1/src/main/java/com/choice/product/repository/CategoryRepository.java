package com.choice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.product.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    //메서드 추가 
}
