package com.choice.ai.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AiAnalysisRequest {
    private MultipartFile image;
}
