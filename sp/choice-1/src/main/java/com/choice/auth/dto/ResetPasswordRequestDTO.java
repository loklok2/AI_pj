package com.choice.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String username;
    private String email;
}