package com.choice.shopping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 엔티티로 설정
@Table(name = "qboard") // 테이블 이름 설정
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Qboard {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long qboardId; // Q&A 게시판 ID

    private Long userId; // 사용자 ID (Foreign Key)
    private String boardType; // 게시판 유형
    private String title; // 게시글 제목
    private String content; // 게시글 내용

    @Column(name = "create_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 게시글 생성 일자

    @Column(name = "edited_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editedDate; // 게시글 수정 일자

    // Getters and Setters (생략 가능)
}
