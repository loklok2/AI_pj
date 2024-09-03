package com.choice.auth.entity;

public enum TokenType {
    ACCESS, // 접근 토큰 (주로 인증을 위해 사용)
    REFRESH, // 갱신 토큰 (주로 접근 토큰 갱신을 위해 사용)
    EMAIL_VERIFICATION, // 이메일 인증 토큰 (회원가입 후 이메일 인증을 위해 사용)
    PASSWORD_RESET; // 비밀번호 재설정 토큰 (비밀번호 재설정을 위해 사용)
}