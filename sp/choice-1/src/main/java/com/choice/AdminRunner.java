package com.choice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor // 생성자를 통해 의존성 주입
public class AdminRunner implements CommandLineRunner {

    private final MemberRepository memberRepository; // MemberRepository 주입
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션이 시작될 때 실행
        // 'admin' 사용자 계정이 존재하지 않는 경우에만 아래 로직 실행
        if (!memberRepository.existsByUsername("admin")) {
            // 새로운 Member 객체를 빌더 패턴을 사용하여 생성
            Member admin = Member.builder()
                    .username("admin") // 사용자 로그인id 설정
                    .password(passwordEncoder.encode("test")) // 비밀번호 암호화 후 설정
                    .name("Admin") // 이름 설정
                    .role(Role.ADMIN) // 관리자 역할 설정
                    .address("관리자") // 주소 설정
                    .phone("010-1234-5678") // 전화번호 설정
                    .email("admin@example.com") // 이메일 설정
                    .enabled(true)
                    .joinDate(LocalDateTime.now()) // 가입 일자 설정
                    .editedDate(LocalDateTime.now()) // 수정 일자 설정
                    .build();

            // 관리자 계정 저장
            memberRepository.save(admin);
        }
    }
}
