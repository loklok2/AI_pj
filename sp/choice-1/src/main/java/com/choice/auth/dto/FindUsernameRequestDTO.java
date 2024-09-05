package com.choice.auth.dto;

import lombok.Data;

@Data
// 아이디 찾기 요청 DTO
public class FindUsernameRequestDTO {
    private String name; // 이름
    private String email; // 이메일
}