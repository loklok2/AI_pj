package com.choice.board.dto;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private Long qboardId;
    private String content;
}
