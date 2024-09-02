package com.sbs.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbs.auth.domain.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    
    // 토큰 값으로 Token 엔티티를 찾기
    Optional<Token> findByTokenValue(String tokenValue);
}
