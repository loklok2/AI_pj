package com.choice.board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long commentId;
    private Long qboardId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime editedDate;
}
