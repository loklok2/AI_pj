package com.choice.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequestDTO {
    private String username; // 사용자 이름
    private String password; // 비밀번호
    private String email; // 이메일
    private String residentRegistrationNumber; // 주민등록번호 앞 7자리
    private String address; // 주소
    private String phone; // 전화번호
    private String nickname; // 닉네임
    // private String role; // 사용자 역할은 기본값으로 MEMBER로 설정
}
