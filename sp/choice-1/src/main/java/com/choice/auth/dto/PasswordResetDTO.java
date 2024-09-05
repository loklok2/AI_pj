package com.choice.auth.dto;

import lombok.Data;

@Data
// 비밀번호 재설정 요청 DTO
public class PasswordResetDTO {
    private String token; // 토큰
    private String newPassword; // 새 비밀번호
}