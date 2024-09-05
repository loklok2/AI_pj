package com.choice.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.shopping.entity.QboardImg;

public interface QboardImgRepository extends JpaRepository<QboardImg, Long> {
    // QboardImg 테이블에서 qboardId를 기준으로 모든 이미지를 찾는 메서드
    List<QboardImg> findByQboardId(Long qboardId);
}
