package com.choice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Q&A 게시글 이미지 DTO
public class QboardImgDTO {
    private Long id; // Q&A 게시글 이미지 ID
    private Long qboardId; // Q&A 게시글 ID
    private String imgName; // 이미지 이름
    private String imgPath; // 이미지 경로
}