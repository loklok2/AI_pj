package com.choice.board.dto;

import java.util.List;

import lombok.Data;

@Data
public class QboardCreateRequest {
    private QboardDTO qboard;
    private List<String> base64Images;
}
