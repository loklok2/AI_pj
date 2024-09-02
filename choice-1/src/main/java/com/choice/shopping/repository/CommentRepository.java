package com.choice.shopping.repository;

import com.choice.shopping.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있음
}