package com.choice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    // 액세스 토큰  
    private String accessToken;
    // 리프레시 토큰
    private String refreshToken;
}