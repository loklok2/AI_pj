package com.sbs.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
	private Long id; // 회원 ID
    private String username; // 사용자 이름 (이메일)
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String token; // 인증 또는 비밀번호 재설정 토큰
    private String newPassword; // 새로운 비밀번호
    private boolean enabled; // 계정 활성화 여부
    private LocalDateTime joinDate; // 가입일자
    private Role role; // 사용자 역할
    private TokenType tokenType; // 토큰 유형 (예: 인증 토큰, 비밀번호 재설정 토큰 등)
    private LocalDateTime expiryDate; // 토큰 만료일자
}
