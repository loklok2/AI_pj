package com.choice.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String username; // 사용자 이름
    private String password; // 비밀번호
}
