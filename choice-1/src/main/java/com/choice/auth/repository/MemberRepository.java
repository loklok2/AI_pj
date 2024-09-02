package com.choice.auth.repository;

import com.choice.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 사용자 이름으로 회원을 검색하는 메서드 선언
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    
    boolean existsByUsername(String username);
}
