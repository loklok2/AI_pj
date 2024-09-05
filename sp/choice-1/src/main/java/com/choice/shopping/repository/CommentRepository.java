package com.choice.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.shopping.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByQboard_QboardId(Long qboardId);

}
