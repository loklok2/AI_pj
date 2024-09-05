package com.choice.shopping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
// Q&A 게시글 DTO
public class QboardDTO {
    private Long id; // Q&A 게시글 ID
    private Long userId; // 사용자 ID
    private String boardType; // 게시글 유형
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private LocalDateTime createDate; // 게시글 생성 날짜
    private LocalDateTime editedDate; // 게시글 수정 날짜
}