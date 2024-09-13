package com.choice.auth.dto;

import lombok.Data;

@Data
// 비밀번호 재설정 요청 DTO
public class ResetPasswordRequestDTO {
    private String username;// 아이디
    private String email;// 이메일
}