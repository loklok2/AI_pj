package com.choice.ai.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.choice.ai.dto.AiAnalysisResponse;

@Service
public class FlaskClientService {

    @Value("${flask.server.url}")
    private String flaskServerUrl;

    @Autowired
    private RestTemplate restTemplate;

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
                flaskServerUrl + "/style_search", requestEntity, AiAnalysisResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Flask 서버 응답 오류: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getAdminRecommendations(List<Map<String, Object>> productDataList) {
        String url = flaskServerUrl + "/admin_search";
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(productDataList),
                new ParameterizedTypeReference<Map<String, Object>>() {
                });
        return response.getBody();
    }

}
