package com.choice.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiAnalysisResult {
    private Long analysisId;
    private List<ProductsDTO> recommendedProducts;
    private List<StyleIndexDTO> styleindex;
    private String captionResult;
}
