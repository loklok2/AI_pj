package com.choice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 로그인 응답 DTO
public class LoginResponseDTO {
    private String accessToken; // 액세스 토큰
    private long accessExpiration; // 액세스 토큰 만료 시간 추가
    private String refreshToken; // 리프레시 토큰
    private long refreshExpiration; // 리프레시 토큰 만료 시간 추가
    private String role; // 권한
    private Long userId; // 유저 아이디
    private String username; // 유저 이름
}