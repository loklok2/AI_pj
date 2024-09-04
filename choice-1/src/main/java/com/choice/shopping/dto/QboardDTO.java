package com.choice.shopping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QboardDTO {
    private Long id;
    private Long userId;
    private String boardType;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime editedDate;
}