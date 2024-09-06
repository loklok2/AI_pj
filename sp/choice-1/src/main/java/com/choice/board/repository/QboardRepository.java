package com.choice.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.board.entity.Qboard;

public interface QboardRepository extends JpaRepository<Qboard, Long> {

}