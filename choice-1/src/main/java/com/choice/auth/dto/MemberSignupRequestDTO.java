package com.choice.auth.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequestDTO {
    private String username;   // 사용자 이름
    private String password;   // 비밀번호
    private String email;      // 이메일
    private String gender;     // 성별
    private Date birthDate;    // 생년월일
    private String address;    // 주소
    private String phone;      // 전화번호
    private String nickname;   // 닉네임
    private String role;       // 사용자 역할
}

