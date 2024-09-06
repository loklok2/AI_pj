package com.choice.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByQboard_QboardId(Long qboardId);

}
