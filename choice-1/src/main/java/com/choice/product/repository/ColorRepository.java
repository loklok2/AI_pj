package com.choice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.product.entity.Color;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있음
}