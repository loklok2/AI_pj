package com.choice.auth.repository;

import com.choice.auth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // 특정 사용자 ID와 토큰 유형으로 토큰을 검색하는 메서드 선언
    Optional<Token> findByUserIdAndTokenType(Long userId, String tokenType);
}
