package com.choice.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.auth.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 사용자 이름으로 회원을 검색하는 메서드 선언
    Optional<Member> findByUsername(String username);

    // 이메일로 회원을 검색하는 메서드 선언
    Optional<Member> findByEmail(String email);

    // 사용자 이름으로 회원을 검색하는 메서드 선언
    boolean existsByUsername(String username);

    // 이름과 이메일로 회원을 검색하는 메서드 선언
    Optional<Member> findByNameAndEmail(String name, String email);

    // 사용자 이름과 이메일로 회원을 검색하는 메서드 선언
    Optional<Member> findByUsernameAndEmail(String username, String email);

    // 이메일 인증 여부와 가입일자로 회원을 검색하는 메서드 선언
    List<Member> findByEnabledAndJoinDateBefore(boolean enabled, LocalDateTime joinDate);
}
