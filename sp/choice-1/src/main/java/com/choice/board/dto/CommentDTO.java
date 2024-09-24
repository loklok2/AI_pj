package com.choice.board.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// 댓글 DTO
public class CommentDTO {
    private Long commentId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime editedDate;
}
