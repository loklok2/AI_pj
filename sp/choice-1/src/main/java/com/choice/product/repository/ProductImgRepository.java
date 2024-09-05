package com.choice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.product.entity.ProductImg;

public interface ProductImgRepository extends JpaRepository<ProductImg, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있음
}
