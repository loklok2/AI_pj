package com.choice.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
    private String gender; // 성별 체크버튼?

    @Temporal(TemporalType.DATE) // 날짜 타입 설정
    private String birthDate; // 생년월일 920101-

    private String address; // 주소
    private String phone; // 전화번호
    private String email; // 이메일
    private String nickname; // 닉네임

    private String style; // 스타일
    private String role; // 사용자 역할

    @Column(name = "join_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime joinDate; // 가입 일자

    @Column(name = "edited_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editedDate; // 수정 일자

}
