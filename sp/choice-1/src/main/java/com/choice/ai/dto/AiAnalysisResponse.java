package com.choice.ai.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiAnalysisResponse {
    @JsonProperty("caption_result")
    private String captionResult;

    @JsonProperty("style_result")
    private List<Integer> styleResult;

    @JsonProperty("recom_result")
    private List<Integer> recomResult;
}
