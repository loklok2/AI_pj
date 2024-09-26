package com.choice.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.choice.board.entity.BoardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Q&A 게시글 DTO
public class QboardDTO {
    private Long id; // Q&A 게시글 ID
    private Long userId; // 사용자 ID
    private String username; // 사용자 이름
    private BoardType boardType; // 게시글 유형
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private List<QboardImgDTO> images; // 게시글 이미지
    private LocalDateTime createDate; // 게시글 생성 날짜
    private LocalDateTime editedDate; // 게시글 수정 날짜
    private List<CommentDTO> comments; // 댓글 목록
}
