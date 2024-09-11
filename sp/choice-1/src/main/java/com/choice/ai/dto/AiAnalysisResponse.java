package com.choice.ai.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AiAnalysisResponse {
    @JsonProperty("style_result")
    private List<Integer> styleResult;

    @JsonProperty("recom_result")
    private List<Integer> recomResult;
}
