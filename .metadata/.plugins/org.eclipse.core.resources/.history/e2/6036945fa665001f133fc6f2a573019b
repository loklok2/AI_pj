package com.sbs.auth.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity // 이 클래스를 JPA 엔티티로 매핑
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 위한 전략 설정 (자동 증가)
    private Long id;

    @Enumerated(EnumType.STRING) // 열거형을 문자열로 매핑
    private TokenType tokenType; // 토큰 유형

    private String tokenValue; // 토큰 값
    private LocalDateTime expiryDate; // 토큰 만료 일자

    @ManyToOne(fetch = FetchType.LAZY) // Member와의 다대일 관계 설정, 지연 로딩 사용
    @JoinColumn(name = "member_id") // 외래 키 설정 (member_id 컬럼)
    private Member member; // 토큰 소유 회원

}
