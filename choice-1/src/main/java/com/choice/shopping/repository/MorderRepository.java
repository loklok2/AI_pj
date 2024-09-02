package com.choice.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.shopping.entity.Morder;

public interface MorderRepository extends JpaRepository<Morder, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있음
}
