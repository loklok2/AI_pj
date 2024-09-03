package com.choice.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String username; // 아이디
    private String email; // 이메일
}