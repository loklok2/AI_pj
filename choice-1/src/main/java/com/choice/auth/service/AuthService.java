package com.choice.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.choice.auth.dto.LoginRequestDTO;
import com.choice.auth.dto.MemberSignupRequestDTO;
import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.config.JWTUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(MemberSignupRequestDTO requestDto) {
        // 사용자 이름 중복 검사
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 사용자 이름입니다.");
        }

        // 이메일 중복 검사
        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 생성 및 저장
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .gender(requestDto.getGender())
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .role(requestDto.getRole())
                .joinDate(LocalDateTime.now())
                .editedDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }
    
    public String login(LoginRequestDTO loginRequestDTO) {
        // 사용자 이름으로 사용자 조회
        Optional<Member> optionalMember = memberRepository.findByUsername(loginRequestDTO.getUsername());
        if (optionalMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
        }

        Member member = optionalMember.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        return JWTUtil.getJWT(member.getUsername());
    }
    
}