package com.choice.shopping.repository;

import com.choice.shopping.entity.ProductImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImgRepository extends JpaRepository<ProductImg, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있음
}
