package com.choice.board.dto;

import lombok.Data;

@Data
// Q&A 게시글 이미지 DTO
public class QboardImgDTO {
    private Long id; // Q&A 게시글 이미지 ID
    private Long qboardId; // Q&A 게시글 ID
    private String imgName; // 이미지 이름
    private String imgPath; // 이미지 경로
}