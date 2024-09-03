package com.choice.auth.repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.auth.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // 특정 사용자 ID와 토큰 유형으로 토큰을 검색하는 메서드 선언
    Optional<Token> findByUserIdAndTokenType(Long userId, String tokenType);

    // 토큰 값으로 토큰을 검색하는 메서드 선언
    Optional<Token> findByTokenValue(String tokenValue);

    // 토큰 유형과 생성 시간으로 토큰을 검색하는 메서드 선언
    List<Token> findByTokenTypeAndCreatedAtBefore(String tokenType, LocalDateTime dateTime);
}
