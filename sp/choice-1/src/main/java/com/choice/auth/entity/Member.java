package com.choice.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 엔티티로 설정
@Table(name = "member") // 테이블 이름 설정
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long userId; // 사용자 식별자

    private String username; // 사용자 아이디
    private String password; // 비밀번호
    private String name; // 이름
    private String gender; // 성별

    @Column(length = 7)
    private String residentRegistrationNumber; // 주민등록번호 앞 7자리

    private String address; // 주소
    private String phone; // 전화번호
    private String email; // 이메일
    // private String nickname; // 닉네임

    @Enumerated(EnumType.STRING)
    private Style style; // 스타일

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 역할

    @Builder.Default
    private boolean enabled = false; // 이메일 인증 여부, 기본값은 false

    @Column(name = "join_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime joinDate; // 가입 일자

    @Column(name = "edited_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editedDate; // 수정 일자

}
