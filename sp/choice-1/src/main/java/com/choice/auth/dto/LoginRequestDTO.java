package com.choice.auth.dto;

import lombok.Data;

@Data
// 로그인 요청 DTO
public class LoginRequestDTO {
    private String username; // 사용자 이름
    private String password; // 비밀번호

}
