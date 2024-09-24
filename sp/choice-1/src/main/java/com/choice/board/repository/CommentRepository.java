package com.choice.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.auth.entity.Role;
import com.choice.board.entity.Comment;
import com.choice.board.entity.Qboard;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByQboard_QboardId(Long qboardId);

    long countByMember_Role(Role role);

    void deleteByQboard(Qboard qboard);
}
