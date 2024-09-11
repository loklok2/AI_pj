package com.choice.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.choice.ai.dto.AiAnalysisResponse;

import java.io.IOException;

@Service
public class FlaskClientService {

    private final RestTemplate restTemplate;
    private final String flaskUrl;

    public FlaskClientService(RestTemplate restTemplate,
            @Value("${flask.api.url}") String flaskUrl) {
        this.restTemplate = restTemplate;
        this.flaskUrl = flaskUrl;
    }

    public AiAnalysisResponse analyzeImage(MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<AiAnalysisResponse> response = restTemplate.postForEntity(
                flaskUrl, requestEntity, AiAnalysisResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Flask 서버 응답 오류: " + response.getStatusCode());
        }
    }
}
