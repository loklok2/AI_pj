package com.choice.shopping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "comment") // 테이블 이름 설정
public class Comment {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long commentId; // 댓글 ID

    private Long qboardId; // 게시글 ID (Foreign Key)
    private Long userId; // 사용자 ID (Foreign Key)
    private String content; // 댓글 내용

    @Column(name = "create_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 댓글 생성 일자

    @Column(name = "edited_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editedDate; // 댓글 수정 일자

}

