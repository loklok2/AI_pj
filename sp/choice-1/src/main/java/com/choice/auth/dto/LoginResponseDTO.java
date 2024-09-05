package com.choice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 로그인 응답 DTO
public class LoginResponseDTO {
    private String accessToken; // 액세스 토큰
    private String refreshToken; // 리프레시 토큰
}
