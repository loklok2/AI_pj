package com.choice.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.choice.auth.entity.Member;
import com.choice.board.entity.Qboard;

public interface QboardRepository extends JpaRepository<Qboard, Long> {
    @Query("SELECT q FROM Qboard q LEFT JOIN FETCH q.comments WHERE q.qboardId = :id")
    Optional<Qboard> findByIdWithComments(@Param("id") Long id);

    List<Qboard> findByMember(Member member);
}