package com.choice.auth.dto;

import com.choice.auth.entity.Style;

import lombok.Data;

@Data
// 회원가입 요청 DTO
public class SignupRequestDTO {
    private String username; // 사용자 이름
    private String password; // 비밀번호
    private String name; // 이름
    private String email; // 이메일
    // private String nickname; // 닉네임
    private String residentRegistrationNumber; // 주민등록번호 앞 7자리
    private String gender; // 성별
    private String address; // 주소
    private String phone; // 전화번호
    private Style style; // 스타일
}